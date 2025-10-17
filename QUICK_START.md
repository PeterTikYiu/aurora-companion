# 🚀 Quick Start Guide - Aurora Companion

## 📋 Current Status
✅ Project structure created  
✅ Dependencies configured  
✅ Core architecture in place  
✅ Theme system ready  
⏳ Ready to build features  

---

## 🎯 Next Steps Checklist

### Immediate Next (Phase 2):
- [ ] Create Room Database setup
- [ ] Define Product entity
- [ ] Define Task entity  
- [ ] Create DAOs
- [ ] Build repositories
- [ ] Set up DataStore preferences
- [ ] Create sample JSON data

### After Database Setup (Phase 3):
- [ ] Build Product List screen
- [ ] Build Product Detail screen
- [ ] Implement search functionality
- [ ] Add navigation
- [ ] Test on emulator

---

## 🛠️ Development Commands

### Build & Run:
```powershell
# Sync Gradle
.\gradlew --refresh-dependencies

# Clean build
.\gradlew clean

# Build debug APK
.\gradlew assembleDebug

# Install and run on device
.\gradlew installDebug

# Run all tests
.\gradlew test

# Check for dependency updates
.\gradlew dependencyUpdates
```

### Code Quality:
```powershell
# Lint check
.\gradlew lint

# Format code (if ktlint added)
.\gradlew ktlintFormat
```

---

## 📂 Project Structure

```
app/src/main/java/com/auroracompanion/
├── core/
│   ├── data/
│   │   └── Result.kt          ← Error handling
│   ├── ui/
│   │   ├── UiState.kt         ← State management
│   │   └── theme/             ← Material 3 theme
│   └── util/
│       ├── Constants.kt       ← App constants
│       └── Extensions.kt      ← Utility functions
│
├── feature/                   ← Features go here (next)
│   ├── product/
│   ├── task/
│   ├── inventory/
│   └── settings/
│
├── di/                        ← Hilt modules (next)
├── navigation/                ← Navigation setup (next)
├── AuroraApplication.kt       ← App class
└── MainActivity.kt            ← Entry point
```

---

## 🎨 Theme Colors

### Light Mode:
- **Primary:** #00897B (Teal)
- **Secondary:** #FDD835 (Yellow)
- **Background:** #FAFAFA

### Dark Mode:
- **Primary:** #4DB6AC (Light Teal)
- **Secondary:** #FDD835 (Yellow)
- **Background:** #1C1B1F

---

## 📝 Coding Conventions

### File Naming:
- Classes: `PascalCase.kt`
- Composables: `PascalCase.kt`
- Extensions: `Extensions.kt`
- Utilities: `SomethingUtil.kt`

### Package Structure:
```
feature/product/
├── data/
│   ├── local/      (entities, DAOs)
│   ├── repository/ (repositories)
│   └── model/      (data models)
├── domain/
│   ├── model/      (domain models)
│   └── usecase/    (business logic)
└── ui/
    ├── components/ (reusable composables)
    ├── screens/    (full screens)
    └── viewmodel/  (ViewModels)
```

### Naming Conventions:
- **Entities:** `ProductEntity` (Room database)
- **Models:** `Product` (domain model)
- **ViewModels:** `ProductViewModel`
- **Repositories:** `ProductRepository`
- **DAOs:** `ProductDao`
- **Use Cases:** `GetProductsUseCase`

---

## 🧪 Testing Strategy

### Unit Tests (70%+ coverage target):
- ✅ ViewModels - test state changes
- ✅ Repositories - test data operations
- ✅ Use Cases - test business logic
- ✅ Extensions - test utility functions

### UI Tests:
- ✅ Critical flows (search, add task)
- ✅ Navigation
- ✅ Form validation

---

## 🐛 Common Issues & Solutions

### Issue: Gradle sync failed
**Solution:** 
```powershell
# Delete .gradle folder
Remove-Item -Recurse -Force .gradle
# Sync again in Android Studio
```

### Issue: Cannot resolve symbols
**Solution:**
1. File → Invalidate Caches → Restart
2. Build → Clean Project
3. Build → Rebuild Project

### Issue: Hilt errors
**Solution:**
- Make sure Application class has `@HiltAndroidApp`
- Make sure MainActivity has `@AndroidEntryPoint`
- Rebuild project after adding Hilt annotations

### Issue: Compose preview not showing
**Solution:**
- Make sure `@Preview` annotation is present
- Click "Build & Refresh" in preview panel
- Add `@Composable` annotation to function

---

## 📚 Key Dependencies Reference

| Library | Version | Purpose |
|---------|---------|---------|
| Compose BOM | 2025.01.00 | UI framework |
| Room | 2.6.1 | Database |
| Hilt | 2.52 | Dependency Injection |
| DataStore | 1.1.1 | Preferences |
| Navigation | 2.8.4 | Screen navigation |
| Coil | 2.7.0 | Image loading |
| Coroutines | 1.8.1 | Async operations |
| Gson | 2.11.0 | JSON parsing |

---

## 🎓 Learning Resources

### Jetpack Compose:
- [Compose Basics](https://developer.android.com/jetpack/compose/tutorial)
- [Compose State](https://developer.android.com/jetpack/compose/state)
- [Compose Navigation](https://developer.android.com/jetpack/compose/navigation)

### Architecture:
- [MVVM Guide](https://developer.android.com/topic/architecture)
- [Repository Pattern](https://developer.android.com/topic/architecture/data-layer)
- [Hilt DI](https://developer.android.com/training/dependency-injection/hilt-android)

### Room Database:
- [Room Guide](https://developer.android.com/training/data-storage/room)
- [Room with Flow](https://developer.android.com/training/data-storage/room/async-queries)

---

## 🎯 Development Workflow

### Adding a New Feature:

1. **Create package structure:**
   ```
   feature/newfeature/
   ├── data/
   ├── domain/
   └── ui/
   ```

2. **Define entity (if needed):**
   ```kotlin
   @Entity(tableName = "new_feature")
   data class NewFeatureEntity(...)
   ```

3. **Create DAO:**
   ```kotlin
   @Dao
   interface NewFeatureDao { ... }
   ```

4. **Build repository:**
   ```kotlin
   class NewFeatureRepository @Inject constructor(...)
   ```

5. **Create ViewModel:**
   ```kotlin
   @HiltViewModel
   class NewFeatureViewModel @Inject constructor(...)
   ```

6. **Build UI:**
   ```kotlin
   @Composable
   fun NewFeatureScreen(...)
   ```

7. **Add navigation route**

8. **Write tests**

---

## 💡 Pro Tips

### Performance:
- Use `remember` to avoid recomposition
- Use `LazyColumn` for long lists
- Implement pagination for large datasets
- Use Coil for image caching

### Code Quality:
- Add KDoc comments for public APIs
- Follow single responsibility principle
- Keep composables small and focused
- Extract reusable components

### Debugging:
- Use `Timber` for logging (add if needed)
- Check Logcat for errors
- Use Android Studio profiler
- Enable strict mode in debug builds

---

## 📞 Support

### If stuck:
1. Check `SETUP_COMPLETE.md` for detailed explanations
2. Review `README.md` for project overview
3. Check Android Developer documentation
4. Ask specific questions!

---

**Ready to code! 🚀** Let's build amazing features!
