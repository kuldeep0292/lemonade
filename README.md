
# Lemonade Stand Order Processing System

This project is a Spring Boot-based backend system for a Lemonade Stand that processes customer orders and provides reports on the sales and available bills. The system allows the processing of orders where customers pay using specific bills (5, 10, or 20 units) and calculates the required change based on the availability of cash in the system.

## Project Description

The Lemonade Stand Order Processing System is designed to facilitate the sale of lemonade, allowing both single-day and multi-day sales tracking. The system handles customer orders, manages payments with various bill denominations, and generates reports on sales activity.

## Features

- Process multiple customer orders for lemonades.
- Supports payment with bills of values 5, 10, and 20.
- Handles change management based on available cash.
- Returns `null` if an order cannot be processed due to insufficient change.
- Provides a report on total lemonades sold, total profit made, and the remaining bills in the system.
- Custom error handling for invalid orders (e.g., invalid bill values).

## Technologies Used

- Java 17
- Spring Boot 3.x
- Maven
- RESTful APIs
- Postman (for API testing)

## Pre-requisites

Before you begin, ensure you have met the following requirements:

- **Java 17** or higher installed on your machine.
- **Maven** installed (for building the project).
- **Postman** or **cURL** (for testing the APIs).

### Important Files:

- **`CustomerOrder.java`**: Model representing a customer order.
- **`OrderProcessor.java`**: Service that processes orders, handles change.
- **`SalesReportGenerator.java`**: Service for handling reports.
- **`LemonadeController.java`**: REST controller exposing APIs for processing orders and getting sales reports.
- **`GlobalExceptionHandler.java`**: Centralized error handling.
- **`InvalidOrderException.java`**: Custom exception for invalid orders.

## How to Run the Project

### 1. Clone the Repository

```bash
git clone https://github.com/kuldeep0292/lemonade.git
cd lemonade-stand
```

### 2. Build the Project

Ensure all dependencies are installed by using Maven:

```bash
mvn clean install
```

### 3. Run the Application

You can run the application using the following Maven command:

```bash
mvn spring-boot:run
```

Alternatively, you can run the `LemonadeStandApplication.java` class directly from your IDE if you are using IntelliJ IDEA, Eclipse, or another Java IDE.

The application will start on `http://localhost:8080`.

## API Documentation

### 1. Process Orders

**Endpoint**: `/api/orders/process`

**Method**: `POST`

**Content-Type**: `application/json`

**Request Body Example**:

```json
[
    {
        "bill_value": 20,
        "position_in_line": 3,
        "requested_lemonades": 4
    },
    {
        "bill_value": 5,
        "position_in_line": 1,
        "requested_lemonades": 1
    },
    {
        "bill_value": 5,
        "position_in_line": 2,
        "requested_lemonades": 1
    }
]
```

**Expected Response**:
- Returns a list of remaining bills or `"null"` if the order cannot be processed due to insufficient change.
- Example: `[5, 5, 20]`

### 2. Get Report

**Endpoint**: `/api/orders/report`

**Method**: `GET`

**Response Example**:
```
Total Lemonades sold - 6
Total Profit Made - 30
Total 5 Bills Remaining - 2
Total 10 Bills Remaining - 0
Total 20 Bills Remaining - 1
```

### Error Handling

- If an invalid bill value (not 5, 10, or 20) is passed in the request, the system will throw a `400 Bad Request` with an appropriate error message.

**Example Error Response**:
```json
{
    "status": 400,
    "message": "Invalid bill value: 0. Accepted values are 5, 10, or 20."
}
```

## Testing

### Using Postman
Pre-requisite - Application should be running. See ## How to Run the Project.

**Process Orders**:

```bash
curl --location --request POST 'http://localhost:8080/api/orders/process' --header 'Content-Type: application/json' --data-raw '[
    { "bill_value": 20, "position_in_line": 3, "requested_lemonades": 4 },
    { "bill_value": 5, "position_in_line": 1, "requested_lemonades": 1 },
    { "bill_value": 5, "position_in_line": 2, "requested_lemonades": 1 }
]'
```

**Get Report**:

```bash
curl --location --request GET 'http://localhost:8080/api/orders/report'
```

### Running Unit Tests

To run the unit tests, navigate to the project directory and execute:

```bash
mvn test
```
OR if using IDE with Junit enabled, then directly navigate to src/test/java -> select test file -> Right click and select Run As -> Jnuit Tests

### JUnit Test Files Overview

1. **`OrderProcessorTests.java`**: 
   - Focuses on testing the order processing logic.
   - Covers scenarios such as valid orders, insufficient change, and invalid bill values.

2. **`ReportSummaryTests.java`**:
   - Tests the reporting functionality.
   - Ensures that sales reports are generated correctly based on processed orders.

Both files leverage JUnit for unit testing, but they focus on different aspects of the application.

## Improvements and Future Enhancements

### Testing Improvements

1. **Add Separate Repo for Testing**: Establish a dedicated repository for testing to maintain a clear separation between production and test code.
2. **Make Use of RestAssured**: Integrate RestAssured for end-to-end testing of APIs to ensure comprehensive coverage.
3. **Host Tests Over Jenkins**: Set up Jenkins to automate the testing process, enabling continuous integration and delivery.
4. **Enable Monitoring and Notifications**: Implement monitoring and notifications over Slack or email to keep the team informed of test results.

### Application Improvements

1. **Add UI**: Develop a front-end interface to enhance user interaction with the application.
2. **Host Over AWS**: Deploy the application on AWS for better scalability and accessibility.
3. **Optimize Exception Handling**: Refine the exception handling process to cover more edge cases and improve user feedback.
4. **Add Logging Support**: Implement logging to track application behavior and troubleshoot issues effectively.
5. **Add Lombok**: Utilize Lombok to reduce boilerplate code in model classes, enhancing code readability.

## Troubleshooting

Here are some common issues you may encounter and how to resolve them:

### 1. Changing the Port Number

If you need to run the application on a different port (other than the default `8080`), you can change the port in the `application.properties` file located in the `src/main/resources/` directory. 

To change the port, add or modify the following line:

```
server.port=8081
```

Replace `8081` with any port number you prefer.

### 2. Port Already in Use

If you encounter the error:

```
Web server failed to start. Port 8080 was already in use.
```

It means another application is already using the default port `8080`. You can either stop the other application or change the port by following the steps mentioned above.

### 3. Testing Issues

- **Postman/Browser not working**: Ensure that the application is running on the correct port (`http://localhost:8080` by default).



