module.exports = {
    mongoClient: null,
    app: null,
    database: "sdinstagram",
    collectionName: "logs",
    init: function (app, dbclient) {
        this.dbclient = dbclient;
        this.app = app;
    },
    getLogs: async function (filter, options) {
        try {
            await this.dbclient.connect();
            const database = this.dbclient.db(this.database);
            const logsCollection = database.collection(this.collectionName);
            const logs = await logsCollection.find(filter, options).toArray();
            return logs;
        } catch (error) {
            throw (error);
        }
    },
    insertLog: async function (log) {
        try {
            await this.dbclient.connect();
            const database = this.dbclient.db(this.database);
            const logsCollection = database.collection(this.collectionName);
            const result = await logsCollection.insertOne(log);
            return result.insertedId;
        } catch (error) {
            throw (error);
        }
    },
    deleteLogs: async function (filter, options) {
        try {
            await this.dbclient.connect();
            const database = this.dbclient.db(this.database);
            const logsCollection = database.collection(this.collectionName);
            const result = await logsCollection.deleteMany(filter, options);
            return result;
        } catch (error) {
            throw (error);
        }
    }
};