module.exports = {
    mongoClient: null,
    app: null,
    database: "sdinstagram",
    collectionName: "friendshipRequests",
    init: function (app, dbClient) {
        this.dbClient = dbClient;
        this.app = app;
    },
    insertFriendshipRequest: async function (friendshipRequest) {
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const friendshipRequestsCollection = database.collection(this.collectionName);
            const result = await friendshipRequestsCollection.insertOne(friendshipRequest);
            return result.insertedId;
        } catch (error) {
            throw (error);
        }
    },
    findFriendshipRequest: async function (filter, options) {
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const friendshipRequestsCollection = database.collection(this.collectionName);
            const result = await friendshipRequestsCollection.findOne(filter, options);
            return result;
        } catch (error) {
            throw (error);
        }
    },
    deleteFriendshipRequest: async function (filter, options) {
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const friendshipRequestsCollection = database.collection(this.collectionName);
            const result = await friendshipRequestsCollection.deleteOne(filter, options);
            return result;
        } catch (error) {
            throw (error);
        }
    },
    getFriendshipRequests: async function (filter, options) {
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const friendshipRequestsCollection = database.collection(this.collectionName);
            const friendshipRequests = await friendshipRequestsCollection.find(filter, options).toArray();
            return friendshipRequests;
        } catch (error) {
            throw (error);
        }
    },
    getFriendshipRequestsPg: async function (filter, options, page) {
        try {
            const limit = 5;
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const friendshipRequestsCollection = database.collection(this.collectionName);
            const cursor = friendshipRequestsCollection.find(filter, options).skip((page - 1) * limit).limit(limit)
            const friendshipRequestsCollectionCount = await cursor.count();
            const requests = await cursor.toArray();
            const result = {requests: requests, total: friendshipRequestsCollectionCount};
            return result;
        } catch (error) {
            throw (error);
        }
    }
};