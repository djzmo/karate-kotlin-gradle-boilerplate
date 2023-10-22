Feature:
  Scenario: Fetch google.com
    Given url 'https://www.google.com'
    When method GET
    Then status 200

    * def data = db.readRow("SELECT `value` FROM test WHERE `key` = 'key'")
    * match data contains { value: "value" }

    * kafka.send({ message: 'hello', info: { first: 5, second: true } })
    * def result = kafka.listen()
    * match result == [{ message: 'hello', info: { first: 5, second: true } }]