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
    }
};