module.exports = {
    mongoClient: null,
    app: null,
    database: "sdinstagram",
    collectionName: "friendship",
    init: function (app, dbClient) {
        this.dbClient = dbClient;
        this.app = app;
    },
    insertFriendship: async function (friendship) {
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const friendshipCollection = database.collection(this.collectionName);
            const result = await friendshipCollection.insertOne(friendship);
            return result.insertedId;
        } catch (error) {
            throw (error);
        }
    },
    findFriendships: async function (filter, options) {
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const friendshipCollection = database.collection(this.collectionName);
            const friendships = await friendshipCollection.find(filter, options).toArray();
            return friendships;
        } catch (error) {
            throw (error);
        }
    },
    findFriendship: async function (filter, options) {
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const friendshipCollection = database.collection(this.collectionName);
            const friendship = await friendshipCollection.findOne(filter, options);
            return friendship;
        } catch (error) {
            throw (error);
        }
    },
    findFriend: async function (filter, options) {
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const friendshipCollection = database.collection(this.collectionName);
            const friendship = await friendshipCollection.findOne(filter, options);
            return friendship;
        } catch (error) {
            throw (error);
        }
    },
    getFriendshipsPg: async function (filter, options, page) {
        try {
            const limit = 5;
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const friendshipCollection = database.collection(this.collectionName);
            const friendshipCollectionCount = await friendshipCollection.countDocuments(filter, options);
            const cursor = friendshipCollection.find(filter, options).skip((page - 1) * limit).limit(limit);
            const friendships = await cursor.toArray();
            const result = { friendships: friendships, total: friendshipCollectionCount };
            return result;
        } catch (error) {
            throw (error);
        }
    },
    deleteFriendshipOfUsers: async function (userIds) {
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const friendshipCollection = database.collection(this.collectionName);
            const result = await friendshipCollection.deleteMany({$or: [{'user1._id': {$in: userIds}}, {'user2._id': {$in: userIds}}]});
            return result;
        } catch (error) {
            throw (error);
        }
    }
};