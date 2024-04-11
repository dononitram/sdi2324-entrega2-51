module.exports = {
    mongoClient: null,
    app: null,
    database: "sdinstagram",
    collectionName: "users",
    init: function (app, dbClient) {
        this.dbClient = dbClient;
        this.app = app;
    },
    findUser: async function(filter, options) {
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const usersCollection  = database.collection(this.collectionName);
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
    findUsers: async function(filter, options) {
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const usersCollection  = database.collection(this.collectionName);

            let result = await usersCollection.find(filter, options).toArray();
            return result;

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
    }
};