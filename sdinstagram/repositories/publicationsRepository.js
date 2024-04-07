module.exports = {
    mongoClient: null,
    app: null,
    database: "sdinstagram",
    collectionName: "publications",
    init: function (app, dbClient) {
        this.dbClient = dbClient;
        this.app = app;
    },
    insertPublication: function (publication, callbackFunction) {
        this.dbClient.connect()
            .then(() => {
                const database = this.dbClient.db(this.database);
                const songsCollection = database.collection(this.collectionName);
                songsCollection.insertOne(publication)
                    .then(result => {
                        callbackFunction(result.insertedId)
                    })
                    .then(() => this.dbClient.close())
                    .catch(err => callbackFunction({error: err.message}));
            })
            .catch(err => callbackFunction({error: err.message}));

    },
    getPublicationsPg: async function(filter, options, page) {
        try {
            const limit = 5;
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const publicationsCollection = database.collection(this.collectionName);
            const publicationsCollectionCount = await publicationsCollection.count();
            const cursor = publicationsCollection.find(filter, options).skip((page - 1) * limit).limit(limit)
            const publications = await cursor.toArray();
            const result = {publications: publications, total: publicationsCollectionCount};
            return result;
        } catch (error) {
            throw (error);
        }
    },
    findPublications: async function(filter, options) {
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const publicationsCollection = database.collection('publications');
            const publications = await publicationsCollection.find(filter, options).toArray();
            return publications;
        } catch (error) {
            throw (error);
        }
    }
}