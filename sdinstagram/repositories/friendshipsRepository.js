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
    }
};