module.exports = {
    mongoClient: null,
    app: null,
    database: "sdinstagram",
    collectionName: "conversations",
    init: function (app, dbClient) {
        this.dbClient = dbClient;
        this.app = app;
    },
    insertConversation: async function(conversation, callbackFunction) {
        this.dbClient.connect()
            .then(() => {
                const database = this.dbClient.db(this.database);
                const conversationsCollection = database.collection(this.collectionName);
                conversationsCollection.insertOne(conversation)
                    .then(result => {
                        callbackFunction(result.insertedId)
                    })
                    .then(() => this.dbClient.close())
                    .catch(err => callbackFunction({error: err.message}));
            })
            .catch(err => callbackFunction({error: err.message}));
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
    },
    updateConversation: async function(conversation, filter, options) {
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const conversationsCollection = database.collection(this.collectionName);
            const result = await conversationsCollection.updateOne(filter, {$set: conversation}, options);
            return result;
        } catch (error) {
            throw (error);
        }
    },
    findConversationByMessageId: async function(messageId){
        try {
            const filter = {"messages": { $elemMatch: {"messageId":messageId}}};
            const options = {};
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const conversationsCollection = database.collection(this.collectionName);
            let result = await conversationsCollection.findOne(filter, options);
            return result;
        } catch (error) {
            throw (error);
        }
    },updateAsReadedByIndexMessage: async function(filter, options, messageIndex){
        try {
            const setStr = "messages."+ messageIndex +".read";
            const update = {};
            update[setStr] = true;
            const options = {};
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const conversationsCollection = database.collection(this.collectionName);
            const result = await conversationsCollection.updateOne(filter, { $set: update }, options);
            return result;
        } catch (error) {
            throw (error);
        }
    }
};