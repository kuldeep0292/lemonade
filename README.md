
# Lemonade Stand Order Processing System

This project is a Spring Boot-based backend system for a Lemonade Stand that processes customer orders and provides reports on the sales and available bills. The system allows the processing of orders where customers pay using specific bills (5, 10, or 20 units) and calculates the required change based on the availability of cash in the system.

## Features

- Process multiple customer orders for lemonades
- Supports payment with bills of values 5, 10, and 20
- Handles change management based on available cash
- Returns null if an order cannot be processed due to insufficient change
- Provides a report on total lemonades sold, total profit made, and the remaining bills in the system
- Custom error handling for invalid orders (e.g., invalid bill values)
  
## Technologies Used

- Java 17
- Spring Boot 3.x
- Maven
- RESTful APIs
- Postman (for API testing)
  
## Prerequisites

Before you begin, ensure you have met the following requirements:

- **Java 17** or higher installed on your machine
- **Maven** installed (for building the project)
- **Postman** or **cURL** (for testing the APIs)
  
## Project Structure

- **`/src/main/java`**: Contains the source code including controllers, services, exception handlers, and models.
- **`/src/main/resources`**: Contains configuration files (application properties).
- **`/src/test/java`**: Contains unit tests for the project.

### Key Files:

- `CustomerOrder.java`: Model representing a customer order.
- `OrderProcessor.java`: Service that processes orders, handles change, and generates reports.
- `LemonadeController.java`: REST controller exposing APIs for processing orders and getting sales reports.
- `GlobalExceptionHandler.java`: Centralized error handling.
- `InvalidOrderException.java`: Custom exception for invalid orders.

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

## API Endpoints

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
        "requested_lemonades": 2
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
- Example: `[5, 20]`

### 2. Get Report

**Endpoint**: `/api/orders/report`

**Method**: `GET`

**Response Example**:
```
Total Lemonades sold - 4
Total Profit Made - 20
Total 5 Bills Remaining - 2
Total 10 Bills Remaining - 0
Total 20 Bills Remaining - 1
```

### Error Handling

- If an invalid bill value (not 5, 10, or 20) is passed in the request, the system will throw a `400 Bad Request` with an appropriate error message.

Example Error Response:
```json
{
    "status": 400,
    "message": "Invalid bill value: 0. Accepted values are 5, 10, or 20."
}
```

## Testing

### Using Postman

1. **Create a new POST request** to `http://localhost:8080/api/orders/process` with the sample input provided above.
2. **Create a new GET request** to `http://localhost:8080/api/orders/report` to get the current sales report.
3. You can test error scenarios by providing invalid bill values (e.g., 0 or 15) in the POST request.

### Using cURL

**Process Orders**:

```bash
curl --location --request POST 'http://localhost:8080/api/orders/process' --header 'Content-Type: application/json' --data-raw '[
    { "bill_value": 20, "position_in_line": 3, "requested_lemonades": 2 },
    { "bill_value": 5, "position_in_line": 1, "requested_lemonades": 1 },
    { "bill_value": 5, "position_in_line": 2, "requested_lemonades": 1 }
]'
```

**Get Report**:

```bash
curl --location --request GET 'http://localhost:8080/api/orders/report'
```

### Running Unit Tests

To run the unit tests:

```bash
mvn test
```

This will run all the tests in the `/src/test` folder and provide the test results in the console.

## Improvements and Future Enhancements

- Add database integration to persist orders and bills data.
- Implement user authentication and authorization for accessing reports.
- Introduce a front-end React UI to interact with the backend services.

## License

This project is licensed under the MIT License. See the `LICENSE` file for more details.
