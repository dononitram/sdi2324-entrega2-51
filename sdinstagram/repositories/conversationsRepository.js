module.exports = {
    mongoClient: null,
    app: null,
    database: "sdinstagram",
    collectionName: "conversations",
    init: function (app, dbClient) {
        this.dbClient = dbClient;
        this.app = app;
    },
    getConversations: async function (filter, options) {
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const conversationsCollection = database.collection(this.collectionName);
            const conversations = await conversationsCollection.find(filter, options).toArray();
            return conversations;
        } catch (error) {
            throw (error);
        }
    },
    findConversation: async function (filter, options) {
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const conversationsCollection = database.collection(this.collectionName);
            return await conversationsCollection.findOne(filter, options);
        } catch (error) {
            throw (error);
        }
    }
};