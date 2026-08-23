# Stock Assistant

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![Playwright](https://img.shields.io/badge/Playwright-1.50.0-45ba4b?logo=playwright&logoColor=white)


A simple Spring Boot application designed to assist with stock market data analysis and monitoring. The application uses automated web scraping, api calls, and scheduled  alerts to help me track my stocks and look for right timing to buy and sell various stock instruments.

Application has only back-end layer and at the moment, I do not plan to add front-end layer. Everything is based on API, mail notifications and access to database.

I do not have rigidly set goals for this application, as the functionality changes within my needs. In the beginning I planned to write it to remind myself how Spring works, although now it is 24/7 running application which is useful for me.


## Features

- 📧 **Email Notifications**: Automated alerts for low prices, owned stock updates, and currency rate changes via Mailgun which is configured for my own domain
- 🔒 **Secure API**: Protected endpoints with Spring Security Basic Authentication
- 📈 **Data Providers**: Real-time data from Yahoo Finance for stock prices
- ⏰ **Scheduling**: Parallel execution of scheduled tasks
- 🗄️ **Persistent Storage**: PostgreSQL database for reliable data management
- 🌐 **RESTful API**: Clean, well-structured endpoints to access app functionality and data

## Technologies Used

**Backend Framework:**
- Java 17
- Spring Boot 3.4.3
- Spring Web (RESTful APIs)
- Spring AOP (Aspect-Oriented Programming)
- Spring Data JPA (Database persistence)

**Security:**
- Spring Security (Basic Authentication)

**Database:**
- PostgreSQL
- Hibernate ORM
- Flyway

**External APIs:**
- Alpha Vantage API - News data
- Yahoo Finance API - Stock market data
- Mailgun API - Email notifications
- NBP API - for currency rates to PLN

**Web Scraping:**
- Microsoft Playwright 1.50.0 - Automated browser control for web content

**Testing:**
- TestNG
- Mockito

**Other Tools:**
- Lombok - Boilerplate code reduction
- JavaCV - Image/video processing
- Maven - Build automation

## Deployment & Infrastructure

**Production Environment:**
- ☁️ **Cloud Hosting**: Deployed on Hetzner VPS (Virtual Private Server)
- 🐧 **Operating System**: Debian Linux configured from scratch
- 🚀 **Deployment Strategy**: Manual deployment via simple custom shell script
- ⚙️ **Process Management**: Running as systemd service for 24/7 uptime
- 🔐 **Security**: Environment-based secrets management, SSH key authentication
- 📊 **Monitoring**: Application logs and scheduled task execution tracking, mail notification whenever unhalded exception happens

### Usage

The application runs several automated and on demand tasks:

- **Low Price Alerts**
    - Monitors configured watched stocks
    - Mail notification alerts when prices drop below threshold percentages
  
- **Owned Stock Alerts**
    - Biweekly check of owned stock performance
    - Mail notification only when price changes more than 30% from purchase value

- **Currency Rate Processing**
  - Monitors USD/PLN currency rate - can be easily modified to track more currency pairs
  - Sends daily mail notifications

- **Trend checker**
    - Monitors Google Trends data for configured countries
    - Sends weekly mail notifications

- **Analyst Insights check**
    - Monitors Yahoo Finance Analyst Insights for stocks with configured watchlist
    - Sends mail notification when current price is below analyst target price

- **Profit report**: on demand via API
    - Compares purchase price and last price of owned stocks, takes purchase and latest currency rate into account and then calculates profit/loss.
  
All scheduled tasks can be run in parallel for optimal performance.


## Highlights

This project demonstrates practical implementation of modern Spring Boot development practices:

✅ **Microservices-Ready Architecture**: Clean separation of concerns with service layers, DTOs, and configuration management

✅ **Advanced Scheduling**: Parallel task execution with ThreadPoolTaskScheduler for efficient resource utilization

✅ **Security**: Protected API endpoints with environment-based credential management

✅ **Web Scraping**: Automated data collection from dynamic websites using Playwright

✅ **Multi-Source Integration**: Aggregation of data from multiple external APIs

✅ **Event-Driven Architecture**: Authentication event listeners for security monitoring and logging

✅ **Production Deployment & DevOps**: Full-stack deployment on Hetzner VPS with Debian Linux configured from scratch, systemd service management, and 24/7 uptime reliability

✅ **Production-Ready Features**: 
- Request/response logging with IP tracking
- Error handling
- Database connection pooling
- Scheduled job management

✅ **RESTful API Design**: Clean endpoint structure with proper HTTP methods and response handling

### Technical Skills Demonstrated

- Enterprise Java Development
- Spring Framework Ecosystem
- RESTful API Design & Implementation
- Database Design & JPA/Hibernate
- Authentication & Authorization
- Task Scheduling & Concurrency
- External API Integration
- Web Scraping & Automation
- Email Service Integration

### Infrastructure Skills Demonstrated

- Linux server administration (Debian)
- VPS setup and configuration from bare metal
- systemd service configuration
- Environment variable management in production
- Shell scripting for deployment automation
- Remote server management via SSH
- Production application monitoring
- Database administration (PostgreSQL on Linux)

## Contact

**Mateusz Kubiś**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-0077B5?style=for-the-badge&logo=linkedin)](https://linkedin.com/in/mateuszkubis1337)
[![GitHub](https://img.shields.io/badge/GitHub-Follow-181717?style=for-the-badge&logo=github)](https://github.com/dacresillvaant)
[![Email](https://img.shields.io/badge/Email-Contact-D14836?style=for-the-badge&logo=gmail)](mailto:mateusz.kubis94@gmail.com)

Feel free to contact me if you have any questions :)
