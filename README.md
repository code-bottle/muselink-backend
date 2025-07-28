# MuseLink

MuseLink is an AI-powered project designed to serve as both an inspiration collaborator and an emotional companion for humans. With the core philosophy of "Inspiration And Emotion Linking Between AI and Humans," MuseLink leverages cutting-edge AI technologies to provide intelligent assistance and emotional support.

> **Powered by AI, Guided by Emotion.**

---

## Features

- **AI Agents**: Includes versatile AI agents like `BottleManus` and `SoulMateApp` for task-solving and emotional support.
- **Tool Integration**: Supports various tools such as web search, PDF generation, terminal operations, and file handling.
- **RAG (Retrieval-Augmented Generation)**: Combines local and cloud-based knowledge retrieval for enhanced responses.
- **MCP Integration**: Leverages Model Context Protocol for external tool execution and resource management, including Amap for map services and Pexels for image search.
- **Sensitive Word Detection**: Ensures safe and appropriate interactions by filtering sensitive content.
- **Memory Management**: Implements conversation memory using MySQL for multi-turn interactions.
- **Extensibility**: Easily extendable with custom tools, advisors, and configurations.

---

## Tech Stack

- **Backend**: Java 21 + Spring Boot 3.4
- **AI Framework**: Spring AI 1.0 (DashScope integration)
- **Database**: MySQL, PostgreSQL (with pgvector for vector storage)
- **Libraries**: Hutool, iText7, Jsoup
- **Build Tool**: Maven
- **Testing**: JUnit 5

---

## Usage

### Endpoints

- **SoulMateApp**: Emotional support chat
  ```http
  GET http://localhost:8123/ai/soul_mate_app/chat/sse?message=I feel stressed
  ```

- **BottleManus**: General-purpose AI assistant
  ```http
  GET http://localhost:8123/ai/manus/chat?message=Plan a trip to Guangzhou
  ```

### Demonstrations

- **SoulMateApp Chat With RAG**:  
  This video demonstrates how RAG enhances responses by combining cloud-based knowledge retrieval.

  <video width="640" height="360" controls>
    <source src="https://github.com/code-bottle/muselink-backend/blob/main/assets/rag_demo.mp4" type="video/mp4">
    Your browser does not support the video tag.
  </video>

- **BottleManus**:  
  This video showcases the capabilities of the BottleManus AI assistant in solving complex tasks.

  <video width="640" height="360" controls>
    <source src="https://github.com/code-bottle/muselink-backend/blob/main/assets/manus_demo.mp4" type="video/mp4">
    Your browser does not support the video tag.
  </video> 

### Example Tools

- **Web Search**: Search the web for information.
- **Web Crawler**: Scrape the content of a web page.
- **PDF Generation**: Create PDFs from Markdown or HTML content.
- **File Operations**: Read and write files.
- **Terminal Commands**: Execute terminal commands programmatically.
- **Resource Download**: Download resources from a given URL.
- **Ask Human**: Request additional input from the user when needed.
- **Terminate**: End the interaction when tasks are completed.

---

## Project Structure

```
muselink-backend/
├── src/
│   ├── main/
│   │   ├── java/com/bottle/muselink/  # Core Java code
│   │   ├── resources/                # Configuration files
│   ├── test/                         # Unit tests
├── sql/                              # SQL scripts for database setup
├── tmp/                              # Temporary files for tools
├── pom.xml                           # Maven configuration
└── README.md                         # Project documentation
```

---

## License

This project is licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).

---