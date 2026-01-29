# 🏦 SistemaBanco (Banking System)

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white)
![Status](https://img.shields.io/badge/Status-Active-success?style=for-the-badge)

A robust banking simulation application developed as part of my **Computer Science** coursework. This project demonstrates the practical application of **JDBC (Java Database Connectivity)**, secure coding practices, and complex SQL logic integration within a Java environment.

---

## 📂 Project Architecture & Features

The system is engineered to simulate real-world financial operations, focusing on security and data integrity:

### 1. 🔐 Security & Database Integration
* **Secure Credential Management:** Implementation of environment isolation using `.properties` files to prevent sensitive data leaks (passwords are strictly kept out of version control).
* **SQL Injection Prevention:** Extensive utilization of `PreparedStatement` for all database queries, ensuring robust defense against common cyber threats.
* **JDBC Connectivity:** Native Java connection handling without external ORM frameworks to demonstrate deep understanding of database interactions.

### 2. 💸 Core Banking Operations
* **PIX System Integration:** Logic for instant transfers using unique keys, bridging user accounts dynamically.
* **Transaction History:** specific SQL queries to retrieve and limit the most recent financial movements for the user interface.
* **Smart Account Management:** Algorithms for automated, collision-free account number generation and real-time balance validation.

### 3. 🏗️ OOP & Design
* **MVC Principles:** Code structured to separate business logic (Model) from user interaction (View) and data processing (Controller).
* **Encapsulation:** Strict protection of user data and balance attributes.

---

## 🛠️ Technologies & Tools
* **Language:** Java (JDK 17+)
* **Database:** MySQL
* **Persistence:** JDBC (Java Database Connectivity)
* **IDE:** IntelliJ IDEA
* **Version Control:** Git

## 🚀 How to Run locally
Since this project uses secure environment variables, follow these steps to set it up:

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/gabrielpaloni/SistemaBanco.git](https://github.com/gabrielpaloni/SistemaBanco.git)
    ```
2.  **Database Setup:**
    Create a MySQL database named `sistemabanco` on your local machine.
3.  **Secure Configuration:**
    * Locate the `db.properties.example` file in the root directory.
    * Rename it to `db.properties`.
    * Open the file and update `db.user` and `db.password` with your local MySQL credentials.
4.  **Execute:**
    Run the `Main.java` file in your IDE.

---

## 👨‍💻 About the Author
**Gabriel Paloni**
*Computer Science Student in Campinas, Brazil.*
Passionate about **Cybersecurity**, **AI**, and **Software Engineering**. Formerly an English Teacher at CNA, now focused on building secure and scalable tech solutions.

---
<p align="center">
  <i>"Code is like humor. When you have to explain it, it’s bad."</i>
</p>
