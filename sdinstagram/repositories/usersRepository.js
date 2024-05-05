module.exports = {
    mongoClient: null,
    app: null,
    database: "sdinstagram",
    collectionName: "users",
    init: function (app, dbClient, publicationRepository, friendshipRepository, friendshipRequestRepository, conversationsRepository) {
        this.dbClient = dbClient;
        this.app = app;
        this.publicationRepository = publicationRepository;
        this.friendshipRepository = friendshipRepository;
        this.friendshipRequestRepository = friendshipRequestRepository;
        this.conversationsRepository = conversationsRepository;
    },
    findUser: async function (filter, options) {
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const usersCollection = database.collection(this.collectionName);
            return await usersCollection.findOne(filter, options);
        } catch (error) {
            throw (error);
        }
    },
    /**
     * Search all the users in the application depending on the filter and options recieved
     * as param
     * @param filter
     * @param options
     * @returns {Promise<*>}
     */
    findUsers: async function (filter, options) {
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const usersCollection = database.collection(this.collectionName);

            let result = await usersCollection.find(filter, options).toArray();
            return result;

        } catch (error) {
            throw (error);
        }
    },
    getUsersPg: async function (filter, options, page) {
        try {
            const limit = 5;
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const usersCollection = database.collection(this.collectionName);
            const usersCollectionCount = await usersCollection.countDocuments(filter, options);
            const cursor = usersCollection.find(filter, options).skip((page - 1) * limit).limit(limit)
            const users = await cursor.toArray();
            const result = { users: users, total: usersCollectionCount };
            return result;
        } catch (error) {
            throw (error);
        }
    },
    getUsers: async function (filter, options) {
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const usersCollection = database.collection(this.collectionName);
            const users = await usersCollection.find(filter, options).toArray();
            return users;
        } catch (error) {
            throw (error);
        }
    },
    insertUser: async function (user) {
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const usersCollection = database.collection(this.collectionName);
            const result = await usersCollection.insertOne(user);
            return result.insertedId;
        } catch (error) {
            throw (error);
        }
    },
    updateUser: async function (filter, update) {
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const usersCollection = database.collection(this.collectionName);
            const result = await usersCollection.updateOne(filter, { $set: update });
            return result;
        } catch (error) {
            throw (error);
        }
    },
    deleteUsers: async function (userIds) {
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const usersCollection = database.collection(this.collectionName);
            const filter = { _id: { $in: userIds } };
            const result = await usersCollection.deleteMany(filter);
            return result;
        } catch (error) {
            throw (error);
        }
    },
    deleteUsersData: async function (userIds) {
        try {
            await this.conversationsRepository.deleteConversationsOfUsers(userIds);
            await this.friendshipRequestRepository.deleteFriendshipRequestsFromUsers(userIds);
            await this.friendshipRepository.deleteFriendshipOfUsers(userIds);
            await this.publicationRepository.deletePublicationsOfUsers(userIds);
            const response = await this.deleteUsers(userIds);
            return response;
        } catch (error) {
            throw (error);
        }
    }
};