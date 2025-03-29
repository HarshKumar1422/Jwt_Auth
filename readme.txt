Step-by-Step Guide to Test Your JWT Authentication Application
Here's how to test your Spring Boot JWT Authentication application:
Prerequisites

Make sure your application is running
Have a tool like Postman, curl, or any REST client ready

Step 1: Start the Application
bashCopymvn spring-boot:run
Verify the application starts without errors.
Step 2: Register a New User
First, register a regular user:

Send a POST request to http://localhost:8080/api/auth/signup
Set the content type to application/json
Use this request body:

jsonCopy{
  "username": "testuser",
  "email": "user@example.com",
  "password": "password123",
  "roles": ["user"]
}

You should receive a successful response like:

jsonCopy{
  "message": "User registered successfully!"
}
Now, register an admin user:

Send another POST request to http://localhost:8080/api/auth/signup
Use this request body:

jsonCopy{
  "username": "adminuser",
  "email": "admin@example.com",
  "password": "admin123",
  "roles": ["admin"]
}
Step 3: Login to Get a JWT Token

Send a POST request to http://localhost:8080/api/auth/signin
Set the content type to application/json
Use this request body for the regular user:

jsonCopy{
  "username": "testuser",
  "password": "password123"
}

You should receive a response with the JWT token:

jsonCopy{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "testuser",
  "email": "user@example.com",
  "roles": ["ROLE_USER"]
}

Save this token for the next steps

Step 4: Access Public Content

Send a GET request to http://localhost:8080/api/test/all
No authentication is needed
You should receive: Public Content.

Step 5: Access User-Protected Content

Send a GET request to http://localhost:8080/api/test/user
Add an Authorization header with the value: Bearer YOUR_JWT_TOKEN
(Replace YOUR_JWT_TOKEN with the token received during login)
You should receive: User Content.

Step 6: Access Admin-Protected Content

First, login as the admin user to get an admin token:

jsonCopy{
  "username": "adminuser",
  "password": "admin123"
}

Send a GET request to http://localhost:8080/api/test/admin
Add an Authorization header with the admin token: Bearer ADMIN_JWT_TOKEN
You should receive: Admin Board.

Step 7: Test Authorization Restrictions

Try to access the admin endpoint with the regular user token
You should receive a 403 Forbidden response

Step 8: Test Token Expiration

If you want to test token expiration, you can:

Modify the jwt.expiration value in your application.yml to a small value (e.g., 60000 for 1 minute)
Wait for the token to expire
Try using the expired token
You should get an unauthorized error



Step 9: Troubleshooting Common Issues
If you encounter issues:

Check the application logs for details
Verify that your H2 database is properly initialized (you can access it at /h2-console)
Make sure your JWT secret is properly configured
Ensure you're including the "Bearer " prefix in the Authorization header

Step 10: Verify Database State

Access the H2 console at http://localhost:8080/h2-console
Connect with the credentials from your application.yml
Check the users and roles tables to verify that your users were created correctly

This comprehensive testing approach will verify that your JWT authentication and authorization system is working as expected.

---------------------------------------------------------------------------------------------

In-Depth Explanation of JwtAuthDemoApplication
Let me walk you through each component of your Spring Boot JWT Authentication application and explain how they work together.
1. Core Application Structure
JwtAuthDemoApplication.java
This is the entry point of your Spring Boot application. The @SpringBootApplication annotation combines:

@Configuration: Tags the class as a source of bean definitions
@EnableAutoConfiguration: Tells Spring Boot to auto-configure the application
@ComponentScan: Scans for components in the application's package

The main() method launches the Spring application context, which bootstraps the entire application.
2. Configuration Layer
application.yml
This configuration file contains all the application settings:

Database connection details for H2
JWT configuration (secret key and expiration time)
Server configuration
JPA and Hibernate settings
Logging configuration

The properties defined here are injected into your application components using @Value annotations or configuration properties classes.
WebSecurityConfig.java
This is a crucial class that configures Spring Security:

Sets up authentication providers (how users are authenticated)
Configures security filter chains (what happens during authentication)
Defines security rules (which endpoints are protected and which are public)
Registers the JWT filter in the filter chain

The filterChain() method defines the security rules and connects all security components. It uses a builder pattern to configure:

CSRF protection (disabled for REST APIs)
Exception handling
Session management (stateless for JWT)
URL authorization rules
JWT filter placement

DbInitializer.java
This configuration class initializes the database with essential data (roles) when the application starts.

Creates a CommandLineRunner bean that executes on startup
Checks if roles exist in the database
Creates default "USER" and "ADMIN" roles if they don't exist

3. Model Layer (Entity Classes)
User.java
The user entity represents users in your application:

Contains user identification information (username, email)
Stores the encrypted password
Maintains a many-to-many relationship with roles
Uses validation annotations to enforce data integrity

Role.java
Represents user roles in your application:

Simple entity with ID and name
Uses an enum (ERole) to ensure consistent role names

ERole.java
An enum defining the possible role types in your application:

ROLE_USER: Regular user permissions
ROLE_ADMIN: Administrator permissions

4. Repository Layer
UserRepository.java
JPA repository interface that provides database operations for users:

Extends JpaRepository to inherit CRUD operations
Defines custom methods for finding users by username
Provides methods to check if a username or email exists

RoleRepository.java
JPA repository interface for role management:

Provides CRUD operations for roles
Contains a method to find roles by name

5. Security Layer
JWT Components
JwtUtils.java
Utility class for JWT operations:

Generates JWT tokens with user details and expiration
Validates JWT tokens
Extracts username from JWT tokens
Handles JWT errors and exceptions

The generateJwtToken() method creates a signed JWT with the user's information, while validateJwtToken() verifies the token's integrity and expiration.
AuthTokenFilter.java
A filter that intercepts requests to check for valid JWTs:

Extends OncePerRequestFilter to ensure it runs once per request
Extracts the JWT from the Authorization header
Validates the JWT using JwtUtils
Loads user details from the UserDetailsService
Sets the authentication in the SecurityContext if the token is valid

This filter is where the magic happens for JWT-based authentication. It processes the token, validates it, and establishes the security context for the request.
AuthEntryPointJwt.java
Handles authentication exceptions:

Implements AuthenticationEntryPoint
Called when an unauthenticated user tries to access protected resources
Returns appropriate HTTP 401 Unauthorized responses

User Details Components
UserDetailsServiceImpl.java
Implements Spring Security's UserDetailsService:

Loads user details from the database
Converts your User entity to Spring Security's UserDetails
Throws exceptions when users aren't found

UserDetailsImpl.java
Custom implementation of Spring Security's UserDetails:

Maps your User entity to Spring Security's expected format
Contains authentication information (username, password, authorities)
Stores user details needed for authentication and authorization

6. Controller Layer
AuthController.java
Handles authentication and user registration:

Provides /api/auth/signin endpoint for user login
Provides /api/auth/signup endpoint for user registration
Uses the authentication manager for user authentication
Generates and returns JWT tokens
Creates new user accounts with proper role assignments

The authenticateUser() method authenticates users and generates JWT tokens, while registerUser() handles user registration with validation.
TestController.java
Provides test endpoints with different authorization requirements:

/api/test/all: Public endpoint accessible to everyone
/api/test/user: Protected endpoint accessible to users and admins
/api/test/admin: Protected endpoint accessible only to admins

Uses @PreAuthorize annotations to enforce role-based access control.
7. Payload Classes (DTOs)
Request DTOs

LoginRequest: Contains username and password for login
SignupRequest: Contains user registration data with validation constraints

Response DTOs

JwtResponse: Returns JWT token and user information after successful login
MessageResponse: Simple message response for operations like registration

8. Request Flow and Authentication Process
Here's how a request flows through your application:

Request Arrives: A client sends a request with a JWT in the Authorization header
AuthTokenFilter Intercepts: The filter extracts and validates the JWT
User Loaded: If the JWT is valid, the filter loads the user's details
Authentication Set: The user's authentication is set in the SecurityContext
Controller Processes: The request reaches the appropriate controller
Authorization Check: The @PreAuthorize annotations check if the user has the required role
Response Returned: The controller processes the request and returns a response

For login, the flow is:

User sends credentials to /api/auth/signin
AuthController authenticates the user using AuthenticationManager
If successful, JwtUtils generates a JWT token
The token and user details are returned to the client

9. Layer Interactions

Controller <-> Service: Controllers depend on services (like UserDetailsService)
Service <-> Repository: Services use repositories to access the database
Security Filters <-> Service: Filters like AuthTokenFilter use services to authenticate users
Configuration <-> All Layers: Configuration beans are used by all layers

10. Security Considerations

Passwords are stored encrypted using BCrypt
JWT tokens are signed with a secret key to prevent tampering
Token expiration limits the window of vulnerability for stolen tokens
Role-based access control restricts access to sensitive endpoints
Stateless authentication eliminates session management issues

This architecture creates a robust, secure authentication and authorization system using industry-standard JWT tokens and Spring Security's powerful features.