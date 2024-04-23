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
                const publicationCollection = database.collection(this.collectionName);
                publicationCollection.insertOne(publication)
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
            options.sort = { date: -1 };
            const cursor = publicationsCollection.find(filter, options).skip((page - 1) * limit).limit(limit)
            const publications = await cursor.toArray();
            const result = {publications: publications, total: publicationsCollectionCount};
            return result;
        } catch (error) {
            throw (error);
        }
    },
    getLastPublicationOf : async function(filter, options){
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const publicationsCollection = database.collection(this.collectionName);
            let result = await publicationsCollection.find(filter, options).sort({ date: -1 }).limit(1).toArray();
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
    },
    deletePublicationsOfUsers : async function(userIds) {
        try {
            await this.dbClient.connect();
            const database = this.dbClient.db(this.database);
            const publicationsCollection = database.collection(this.collectionName);
            const filter = {"author._id": { $in: userIds }};
            const result = await publicationsCollection.deleteMany(filter);
            return result;
        } catch (error) {
            throw (error);
        }
    }
}