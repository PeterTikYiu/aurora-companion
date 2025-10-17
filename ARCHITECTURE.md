# 🏗️ Aurora Companion - Architecture Overview

## 📊 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                     │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐            │
│  │  Product   │  │    Task    │  │  Inventory │            │
│  │   Screen   │  │   Screen   │  │   Screen   │            │
│  └──────┬─────┘  └──────┬─────┘  └──────┬─────┘            │
│         │                │                │                   │
│  ┌──────▼────────────────▼────────────────▼─────┐           │
│  │          ViewModels (State Management)        │           │
│  └──────┬────────────────┬────────────────┬──────┘           │
└─────────┼────────────────┼────────────────┼──────────────────┘
          │                │                │
┌─────────┼────────────────┼────────────────┼──────────────────┐
│         │     DOMAIN LAYER (Business Logic)                   │
│  ┌──────▼─────┐   ┌──────▼─────┐   ┌──────▼─────┐           │
│  │   Search   │   │   Create   │   │   Update   │           │
│  │  Products  │   │    Task    │   │   Stock    │           │
│  │  UseCase   │   │  UseCase   │   │  UseCase   │           │
│  └──────┬─────┘   └──────┬─────┘   └──────┬─────┘           │
└─────────┼────────────────┼────────────────┼──────────────────┘
          │                │                │
┌─────────┼────────────────┼────────────────┼──────────────────┐
│         │         DATA LAYER (Data Management)                │
│  ┌──────▼────────────────▼────────────────▼─────┐            │
│  │              Repositories                     │            │
│  │  (Single Source of Truth - abstracts data)   │            │
│  └──────┬─────────────────────────────────┬──────┘            │
│         │                                  │                   │
│  ┌──────▼────────┐                 ┌──────▼────────┐         │
│  │  Room Database│                 │   DataStore   │         │
│  │   (SQLite)    │                 │ (Preferences) │         │
│  └───────────────┘                 └───────────────┘         │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔄 Data Flow Pattern

### Unidirectional Data Flow:

```
┌──────────────────────────────────────────────────────┐
│                    User Action                        │
│                  (Click, Type, etc.)                  │
└─────────────────────┬────────────────────────────────┘
                      │
                      ▼
┌──────────────────────────────────────────────────────┐
│                   Composable                          │
│         (Calls ViewModel function)                    │
└─────────────────────┬────────────────────────────────┘
                      │
                      ▼
┌──────────────────────────────────────────────────────┐
│                   ViewModel                           │
│      (Processes action, updates state)                │
└─────────────────────┬────────────────────────────────┘
                      │
                      ▼
┌──────────────────────────────────────────────────────┐
│             Repository / UseCase                      │
│        (Business logic, data operations)              │
└─────────────────────┬────────────────────────────────┘
                      │
                      ▼
┌──────────────────────────────────────────────────────┐
│              Data Source                              │
│         (Room Database / DataStore)                   │
└─────────────────────┬────────────────────────────────┘
                      │
                      ▼
┌──────────────────────────────────────────────────────┐
│                Flow/StateFlow                         │
│            (Reactive data stream)                     │
└─────────────────────┬────────────────────────────────┘
                      │
                      ▼ (Automatic update)
┌──────────────────────────────────────────────────────┐
│                   ViewModel                           │
│            (Receives data updates)                    │
└─────────────────────┬────────────────────────────────┘
                      │
                      ▼ (Emits new state)
┌──────────────────────────────────────────────────────┐
│                   Composable                          │
│              (UI recomposes automatically)            │
└──────────────────────────────────────────────────────┘
```

---

## 🎯 Feature Module Structure

### Example: Product Feature

```
feature/product/
│
├── data/                           ← DATA LAYER
│   ├── local/
│   │   ├── ProductEntity.kt        ← Room entity
│   │   └── ProductDao.kt           ← Database access
│   ├── repository/
│   │   └── ProductRepository.kt    ← Data abstraction
│   └── model/
│       └── ProductDto.kt           ← Data transfer object
│
├── domain/                         ← DOMAIN LAYER
│   ├── model/
│   │   └── Product.kt              ← Domain model
│   └── usecase/
│       ├── SearchProductsUseCase.kt
│       ├── GetProductByIdUseCase.kt
│       └── UpdateStockUseCase.kt
│
└── ui/                             ← PRESENTATION LAYER
    ├── components/
    │   ├── ProductCard.kt          ← Reusable component
    │   ├── ProductListItem.kt
    │   └── StockBadge.kt
    ├── screens/
    │   ├── ProductListScreen.kt    ← Full screen composable
    │   ├── ProductDetailScreen.kt
    │   └── ProductSearchScreen.kt
    └── viewmodel/
        ├── ProductViewModel.kt     ← State management
        └── ProductUiState.kt       ← UI state definition
```

---

## 🔌 Dependency Injection Graph

### Hilt Component Hierarchy:

```
SingletonComponent (Application-scoped)
├── AuroraDatabase
├── ProductDao
├── TaskDao
├── DataStore
├── ProductRepository
└── TaskRepository
    │
    ├── ViewModelComponent (ViewModel-scoped)
    │   ├── ProductViewModel
    │   │   └── GetProductsUseCase
    │   │   └── SearchProductsUseCase
    │   │   └── ProductRepository
    │   │
    │   └── TaskViewModel
    │       └── GetTasksUseCase
    │       └── CreateTaskUseCase
    │       └── TaskRepository
    │
    └── ActivityComponent (Activity-scoped)
        └── MainActivity
```

---

## 📱 Screen Navigation Flow

```
                    ┌──────────────┐
                    │   Splash     │
                    │   Screen     │
                    └──────┬───────┘
                           │
                           ▼
                    ┌──────────────┐
              ┌────►│    Login     │
              │     │   Screen     │
              │     └──────┬───────┘
              │            │
              │            ▼
              │     ┌──────────────┐
              │     │    Home/     │
              │     │  Dashboard   │◄────────┐
              │     └──────┬───────┘         │
              │            │                 │
              │     ┌──────┴─────────────┐   │
              │     │                    │   │
              │     ▼                    ▼   │
              │ ┌─────────┐         ┌────────┐
              │ │ Product │         │  Task  │
              │ │  List   │         │  List  │
              │ └────┬────┘         └────┬───┘
              │      │                   │
              │      ▼                   ▼
              │ ┌─────────┐         ┌────────┐
              │ │ Product │         │  Task  │
              │ │ Detail  │         │ Detail │
              │ └─────────┘         └────────┘
              │
              │     ┌──────────────┐
              └─────┤   Settings   │
                    └──────────────┘
```

---

## 🗄️ Database Schema

### Room Database Tables:

```sql
┌─────────────────────────────────────────┐
│             products                     │
├─────────────┬───────────┬───────────────┤
│ id          │ INTEGER   │ PRIMARY KEY   │
│ sku         │ TEXT      │ UNIQUE        │
│ name        │ TEXT      │ NOT NULL      │
│ category    │ TEXT      │ NOT NULL      │
│ price       │ REAL      │ NOT NULL      │
│ stockQty    │ INTEGER   │ NOT NULL      │
│ description │ TEXT      │               │
│ imageUri    │ TEXT      │               │
│ lastModified│ INTEGER   │ NOT NULL      │
└─────────────┴───────────┴───────────────┘

┌─────────────────────────────────────────┐
│               tasks                      │
├─────────────┬───────────┬───────────────┤
│ id          │ INTEGER   │ PRIMARY KEY   │
│ title       │ TEXT      │ NOT NULL      │
│ description │ TEXT      │               │
│ priority    │ TEXT      │ NOT NULL      │
│ assignedTo  │ TEXT      │               │
│ dueDate     │ INTEGER   │               │
│ isCompleted │ INTEGER   │ NOT NULL      │
│ createdAt   │ INTEGER   │ NOT NULL      │
│ completedAt │ INTEGER   │               │
└─────────────┴───────────┴───────────────┘

┌─────────────────────────────────────────┐
│         stock_changes                    │
├─────────────┬───────────┬───────────────┤
│ id          │ INTEGER   │ PRIMARY KEY   │
│ productId   │ INTEGER   │ FOREIGN KEY   │
│ quantityDiff│ INTEGER   │ NOT NULL      │
│ reason      │ TEXT      │               │
│ timestamp   │ INTEGER   │ NOT NULL      │
│ staffName   │ TEXT      │               │
└─────────────┴───────────┴───────────────┘
```

### Indexes (for performance):
```sql
CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_tasks_priority ON tasks(priority);
CREATE INDEX idx_tasks_due_date ON tasks(dueDate);
CREATE INDEX idx_stock_changes_product ON stock_changes(productId);
```

---

## 🔄 State Management Pattern

### ViewModel State Flow:

```kotlin
// In ViewModel:
┌────────────────────────────────────────────┐
│  private val _uiState = MutableStateFlow   │
│  val uiState: StateFlow = _uiState         │
└────────────────────────────────────────────┘
                    │
                    ▼
┌────────────────────────────────────────────┐
│         sealed interface UiState           │
│  ├── Loading                               │
│  ├── Success(data: T)                      │
│  ├── Error(message: String)                │
│  └── Empty                                 │
└────────────────────────────────────────────┘
                    │
                    ▼
┌────────────────────────────────────────────┐
│  // In Composable:                         │
│  val state by viewModel.uiState            │
│      .collectAsState()                     │
│                                            │
│  when (state) {                            │
│    Loading -> ShowLoading()                │
│    Success -> ShowContent()                │
│    Error -> ShowError()                    │
│    Empty -> ShowEmptyState()               │
│  }                                         │
└────────────────────────────────────────────┘
```

---

## 🧩 Component Relationships

### Product Feature Example:

```
ProductListScreen
    ↓ uses
ProductViewModel
    ↓ injects
SearchProductsUseCase
    ↓ uses
ProductRepository
    ↓ uses
┌──────────────┬────────────────┐
│   ProductDao │  JSON Parser   │
└──────────────┴────────────────┘
```

---

## 📦 Dependency Graph

```
┌──────────────────────────────────────────────────┐
│              Application Module                   │
│  @HiltAndroidApp                                 │
│  AuroraApplication                               │
└────────────────────┬─────────────────────────────┘
                     │
     ┌───────────────┴──────────────────┐
     │                                   │
┌────▼─────────────┐        ┌──────────▼──────────┐
│ Database Module  │        │ DataStore Module    │
│  @InstallIn      │        │   @InstallIn        │
│  SingletonComp.  │        │   SingletonComp.    │
├──────────────────┤        ├─────────────────────┤
│ • AuroraDatabase │        │ • DataStore         │
│ • ProductDao     │        │ • Preferences Repo  │
│ • TaskDao        │        └─────────────────────┘
└────┬─────────────┘
     │
┌────▼─────────────┐
│ Repository Module│
│   @InstallIn     │
│   SingletonComp. │
├──────────────────┤
│ • Product Repo   │
│ • Task Repo      │
└────┬─────────────┘
     │
     └─────────────────┐
                       │
              ┌────────▼────────┐
              │ ViewModels      │
              │  @HiltViewModel │
              ├─────────────────┤
              │ • Product VM    │
              │ • Task VM       │
              └─────────────────┘
```

---

## 🎨 UI Component Hierarchy

```
MainActivity
 └── AuroraCompanionTheme
      └── NavHost
           ├── HomeScreen
           │    ├── TopAppBar
           │    ├── BottomNavBar
           │    └── Content
           │
           ├── ProductListScreen
           │    ├── TopAppBar
           │    │    ├── Title
           │    │    └── SearchIcon
           │    ├── SearchBar
           │    └── LazyColumn
           │         └── ProductCard (reusable)
           │              ├── ProductImage
           │              ├── ProductInfo
           │              └── StockBadge
           │
           ├── ProductDetailScreen
           │    ├── TopAppBar
           │    │    └── BackButton
           │    ├── ProductImage
           │    ├── ProductDetails
           │    ├── StockInfo
           │    └── ActionButtons
           │
           └── TaskListScreen
                ├── TopAppBar
                ├── FilterChips
                └── LazyColumn
                     └── TaskCard (reusable)
                          ├── TaskInfo
                          ├── PriorityBadge
                          └── CompleteButton
```

---

## 🔒 Data Access Control

### Repository Pattern:

```
┌─────────────────────────────────────────────┐
│           ViewModels (UI Layer)              │
│        Can only access repositories          │
└────────────────┬────────────────────────────┘
                 │ ❌ Cannot access DAOs directly
                 │ ❌ Cannot access Database
                 │ ✅ Can access Repositories
                 │
┌────────────────▼────────────────────────────┐
│        Repositories (Data Layer)             │
│   Single Source of Truth for data            │
│   • Abstracts data sources                   │
│   • Handles caching logic                    │
│   • Combines multiple sources                │
└────────────────┬────────────────────────────┘
                 │
     ┌───────────┴──────────────┐
     │                          │
┌────▼─────────┐       ┌────────▼────────┐
│ Room Database│       │   Remote API    │
│  (Local)     │       │   (Future)      │
└──────────────┘       └─────────────────┘
```

---

## 🚦 Error Handling Flow

```
Data Source (DAO/API)
    │ throws Exception
    ▼
Repository
    │ catches Exception
    │ wraps in Result.Error
    ▼
ViewModel
    │ receives Result.Error
    │ updates UiState.Error
    ▼
Composable
    │ observes UiState.Error
    │ shows error UI (Snackbar)
    │ offers retry action
    ▼
User taps retry
    │
    └──► ViewModel retries operation
```

---

## 📊 Testing Pyramid

```
                    ┌──────┐
                    │  E2E │  (5%)
                    │ UI   │
                    └──┬───┘
                  ┌────┴────┐
                  │Integration│ (15%)
                  │  Tests    │
                  └────┬─────┘
              ┌────────┴────────┐
              │   Unit Tests    │ (80%)
              │                 │
              │ • ViewModels    │
              │ • Repositories  │
              │ • Use Cases     │
              │ • Extensions    │
              └─────────────────┘
```

---

## 🔄 Build Process Flow

```
Source Code (.kt files)
    ↓ Kotlin Compiler
Bytecode (.class files)
    ↓ R8/ProGuard (Release)
Optimized Bytecode
    ↓ DEX Compiler
Dalvik Bytecode (.dex)
    ↓ APK Builder
Android Package (.apk)
    ↓ APK Signer
Signed APK
    ↓ Install
App on Device
```

---

## 🎯 This Architecture Provides:

✅ **Separation of Concerns** - Each layer has clear responsibility  
✅ **Testability** - Easy to write unit tests  
✅ **Maintainability** - Changes don't ripple through entire codebase  
✅ **Scalability** - Easy to add new features  
✅ **Reusability** - Components can be reused  
✅ **Type Safety** - Compile-time checks prevent errors  
✅ **Reactive** - UI automatically updates with data  
✅ **Offline-First** - Works without internet  

---

**This is production-grade architecture used by major companies! 🚀**
