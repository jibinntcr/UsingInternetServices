Here is the complete `README.md` file for your **Using Internet Services** project.

```markdown
# SDPD CS #08: Using Internet Services 🌐

**WILP BITS Pilani** | Mobile Application Development

This Android application demonstrates the **Client-Server Architecture**. It shows how an Android app (Client) connects to a remote Backend (Service) to fetch data, while keeping the business logic separate.

---

## 🧐 The Core Concept

### 1. Client vs. Service
* **Android App (Client):** This is the "Front-end". It is responsible for:
    * [cite_start]Initiating requests (e.g., "Get Users") [cite: 208-209].
    * [cite_start]Validating responses[cite: 210].
    * [cite_start]Updating the UI[cite: 211].
    * [cite_start]**Note:** The Client never accesses the database directly[cite: 212].
* **Internet Service (Backend):** This is the "Remote System". It is responsible for:
    * [cite_start]Processing business logic [cite: 220-221].
    * [cite_start]Talking to the Database[cite: 222].
    * [cite_start]Sending back JSON data[cite: 223].

### 2. Architectural Stability
The Android code remains **identical** regardless of where the service is hosted.
* [cite_start]Whether the backend is on your **Local Machine**, a **Production Server**, or the **Cloud** (AWS/Google App Engine) [cite: 341-343].
* [cite_start]Only the `BASE_URL` changes; the rest of the app logic stays the same[cite: 346, 324].

---

## 🛠️ Project Setup

### 1. Dependencies
We use **Retrofit** and **Moshi** to handle the connection and data parsing.
Add these to `build.gradle.kts`:
```kotlin
dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")
}

```

### 2. Permissions

The app requires Internet access to reach the service.
Add this to `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />

```

---

## 💻 Code Structure

### Step 1: The Data Model (`User`)

Defines the structure of the data we expect from the service.

```kotlin
data class User(val id: Int, val name: String, val email: String)

```

### Step 2: The API Contract (`ApiService`)

Defines *how* we talk to the service (Endpoints & HTTP Methods) .

```kotlin
interface ApiService {
    @GET("users") // Endpoint: /users
    suspend fun getUsers(): List<User>
}

```

### Step 3: Service Connection (`ServiceClient`)

Configures Retrofit. This is where you would change the `BASE_URL` if moving from Localhost to Cloud.

```kotlin
object ServiceClient {
    private const val BASE_URL = "[https://jsonplaceholder.typicode.com/](https://jsonplaceholder.typicode.com/)"
    // ... Retrofit Builder logic ...
}

```

### Step 4: UI with Pagination

* **Connect:** Fetches data using Coroutines.
* **Pagination:** Displays users in small groups (3 per page) using `LazyColumn`.
* **Reset:** A "Go Home" button clears the list and returns to the start screen.

---

## 🚀 How to Run

1. **Clone** this repository.
2. **Open** in Android Studio.
3. **Sync Gradle** to install Retrofit & Moshi.
4. **Run** on an Emulator or Device.
5. Click **"Connect to Service"** to fetch data.
6. Use **"Next/Prev"** to navigate pages.
7. Click the **"X Close"** button to reset the app.

```

```


<img width="1249" height="781" alt="Screenshot 2026-02-13 at 10 42 13 AM" src="https://github.com/user-attachments/assets/0a2d0370-9877-41c9-bf4e-9384965061a3" />

<img width="1251" height="778" alt="Screenshot 2026-02-13 at 10 42 24 AM" src="https://github.com/user-attachments/assets/f2a9fc73-90ce-463c-b17f-45d9cda214e3" />

