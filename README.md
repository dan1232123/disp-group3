# 🚗 Car Repair Shop Case Study – BPM with Camunda

This case study demonstrates how we modeled and automated a car repair shop's customer journey using **Camunda** for BPMN (Business Process Management Notation). The process includes all nesaccery operations — all backed by a custom database to track operations.

---

## 🛠 Overview

At our car repair shop, the customer's journey is designed to be smooth, transparent, and efficient. Whether a customer walks in, calls for help, or needs towing, our process ensures a high level of service with clear communication.

---

## 🧠 What We Built

- **Operational BPM with Camunda**:
  - Modeled the full lifecycle of a customer journey.
  - Integrated service tasks to handle real-time operations like diagnostics, towing, and repair.
  - Decision gateways evaluate membership status for discounts and offers.

- **Custom Backend with Database**:
  - Created a relational database with three core tables:
    - `members`: Stores membership data and benefits.
    - `issues`: Logs vehicle issues and diagnostics.
    - `transactions`: Tracks payments, deposits, and repairs.

---

## 🧾 Key Business Logic

- **Membership Program**:
  - £50/year for benefits like discounts and a free MOT.
  - Membership status is checked at multiple points: before diagnostics, towing quotes, and final billing.

- **Diagnostics & Repair Flow**:
  - A non-refundable deposit is collected to begin diagnostics.
  - Repairs proceed only after customer approval.
  - Includes post-repair testing and a complimentary two-week warranty.

- **Towing Services**:
  - Seamless intake for breakdown calls.
  - Precise quoting and dispatch, with membership discounts applied.

- **Customer Assurance**:
  - If repairs fall short, customers can request free follow-up fixes or a full refund.

---

## 🔗 Technologies Used

- **Camunda Platform** – BPMN modeling and process orchestration  
- **SpringBoot** – Java Framework 
- **Zeebe** – Java integration with Camunda

---


