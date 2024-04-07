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

    }
}