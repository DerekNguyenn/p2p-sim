# P2P File Sharing Simulation

This project simulates a **peer-to-peer (P2P) file sharing network** using JavaFX. It provides a visual and interactive environment to observe how files are distributed chunk-by-chunk across various types of peers such as Seeders, Leechers, Clients, and Supernodes.

![demo](https://github.com/user-attachments/assets/6306bee1-71b3-4fba-a581-b7056aa631c6)

---

## Features

- **Simulation of realistic P2P networks** with peer churn, bandwidth variation, and random topology generation.
- **Customizable parameters**: initial peer count, file size, chunk size, and simulation speed.
- **Different peer roles**:
    - **Client**: Target node that initiates download.
    - **Seeder**: Fully owns all chunks and uploads to others.
    - **Leecher**: Downloads missing chunks from others.
    - **Supernode**: High-capacity peer acting as a fast relay hub.
- **JavaFX GUI**: Interactive setup and real-time network transfer visualization with progress tracking.
- **Automatic stall detection** when downloads become near-impossible.

- For more information, please view the [JavaDoc](https://dereknguyenn.github.io/p2p-sim/)

---

## Getting Started

### Prerequisites

- Java 17 or later
- JavaFX SDK (included or must be configured with your IDE)

### Clone the Repo

```bash
git clone https://github.com/DerekNguyenn/p2p-sim.git
cd p2p-sim
