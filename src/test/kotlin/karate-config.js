function fn() {
    var DbUtils = Java.type('org.example.test.utils.DbUtils');
    var KafkaUtils = Java.type('org.example.test.utils.KafkaUtils');
    var dbConfig = {
        url: 'jdbc:mysql://localhost:3306/test_database',
        username: 'root',
        password: 'password',
        driverClassName: 'com.mysql.cj.jdbc.Driver'
    };
    var serverPort = karate.properties['server.port'];
    return {
        urlBase: 'http://localhost:' + serverPort,
        db: new DbUtils(dbConfig),
        kafka: new KafkaUtils('test-topic')
    };
}
