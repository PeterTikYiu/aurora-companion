# Phase 6: Inventory Management - Implementation Plan

**Status**: 📋 **PLANNED**  
**Estimated Duration**: 2-3 weeks  
**Priority**: High (Core MVP Feature)  
**Dependencies**: Phases 1-5 complete ✅

---

## 🎯 Phase 6 Goals

Implement a **comprehensive Inventory Management System** that allows Pets at Home store staff to:
- View current stock levels across all products
- Adjust stock quantities (add/remove inventory)
- Track stock movements with audit history
- Receive low-stock alerts
- Perform stock searches and filtering
- Generate basic inventory reports

This completes the **core MVP functionality** as outlined in `PROJECT_DETAIL.md`.

---

## 📋 Feature Requirements

### Must Have (MVP)
1. **Inventory List Screen**
   - Display all products with current stock levels
   - Visual indicators for stock status (In Stock / Low Stock / Out of Stock)
   - Search by product name or SKU
   - Filter by category and stock status
   - Sort by name, stock level, or category

2. **Stock Adjustment Screen**
   - Add stock (receiving shipments)
   - Remove stock (sales, damage, expiry)
   - Adjustment reason dropdown (Received, Sold, Damaged, Expired, Correction)
   - Quantity input with validation
   - Notes field for additional context
   - Confirmation dialog for large adjustments

3. **Low Stock Alerts**
   - Configurable threshold per product (default: 10 units)
   - Badge/indicator on inventory list for low stock items
   - Dedicated "Low Stock" filter tab
   - Alert count in bottom navigation badge

4. **Stock History**
   - View adjustment history per product
   - Show date, quantity change, reason, notes, and staff member
   - Filter history by date range and adjustment type
   - Accessible from product detail screen

5. **Product Detail Enhancements**
   - Current stock level prominently displayed
   - Quick stock adjustment button
   - Stock history timeline
   - Low stock threshold setting

### Should Have (Nice to Have)
6. **Batch Operations**
   - Bulk stock adjustments (CSV import or multi-select)
   - Mark multiple items for reorder

7. **Stock Reports**
   - Current inventory value (qty × price)
   - Low stock report (exportable)
   - Stock movement summary (daily/weekly)

8. **Notifications** (if time permits)
   - Local notifications for low stock alerts
   - Daily inventory summary notification

### Could Have (Future Enhancement)
9. **Advanced Features**
   - Stock forecasting based on historical data
   - Barcode scanner for quick stock updates
   - Expiry date tracking for perishables
   - Supplier integration for automatic reordering

---

## 🏗️ Technical Architecture

### Database Schema Updates

#### New Entity: `StockMovement`
```kotlin
@Entity(
    tableName = "stock_movements",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["productId"]),
        Index(value = ["timestamp"]),
        Index(value = ["movementType"])
    ]
)
data class StockMovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val quantityChange: Int, // Positive for additions, negative for removals
    val movementType: String, // RECEIVED, SOLD, DAMAGED, EXPIRED, CORRECTION
    val reason: String?,
    val staffMember: String?,
    val timestamp: Long = System.currentTimeMillis()
)
```

#### Update: `ProductEntity`
```kotlin
// Add new fields:
val minStockLevel: Int = 10, // Low stock threshold
val lastStockUpdate: Long? = null // Track last adjustment
```

### New Files to Create

#### Data Layer (3 files)
1. **`StockMovement.kt`** (~40 lines)
   - Domain model for stock movements
   - Movement type enum

2. **`StockMovementDao.kt`** (~120 lines)
   - Insert movement
   - Get movements by product
   - Get movements by date range
   - Get low stock products
   - Get stock summary

3. **`InventoryRepository.kt`** (~200 lines)
   - Adjust stock with audit trail
   - Get product inventory
   - Search and filter inventory
   - Get low stock alerts
   - Get stock history
   - Calculate inventory metrics

#### UI Layer - ViewModel (2 files)
4. **`InventoryViewModel.kt`** (~250 lines)
   - Inventory list state management
   - Search and filter logic
   - Low stock alert count
   - Stock status calculations

5. **`StockAdjustmentViewModel.kt`** (~180 lines)
   - Stock adjustment form state
   - Validation logic
   - Submit adjustment with audit

#### UI Layer - Screens (3 files)
6. **`InventoryListScreen.kt`** (~300 lines)
   - Inventory grid/list with stock levels
   - Search bar
   - Filter chips (All / Low Stock / Out of Stock / By Category)
   - Stock status badges
   - Pull-to-refresh

7. **`StockAdjustmentScreen.kt`** (~250 lines)
   - Adjustment type selector (Add / Remove)
   - Quantity input
   - Reason dropdown
   - Notes field
   - Confirmation dialog

8. **`StockHistoryScreen.kt`** (~200 lines)
   - Timeline view of stock movements
   - Filter by date range
   - Movement type chips
   - Export button (future)

#### UI Layer - Components (1 file)
9. **`InventoryComponents.kt`** (~350 lines)
   - `StockLevelCard` - Visual stock indicator
   - `StockStatusBadge` - In Stock / Low / Out badge
   - `StockMovementItem` - History timeline item
   - `InventoryMetricsCard` - Summary statistics
   - `LowStockAlertBanner` - Alert notification
   - `StockAdjustmentDialog` - Quick adjust dialog

#### Navigation & Integration
10. **Update `Screen.kt`** - Add Inventory routes
11. **Update `NavGraph.kt`** - Wire up Inventory navigation
12. **Update `MainScreen.kt`** - Add Inventory tab to bottom nav (3rd tab)
13. **Update `ProductDetailScreen.kt`** - Add stock adjustment button

#### Database & Seeding
14. **Update `DatabaseSeeder.kt`** - Set realistic stock levels for products
15. **Create `stock_movements.json`** - Optional: seed sample stock history

---

## 📊 Implementation Timeline

### Week 1: Data Layer & Core Logic
**Days 1-2: Database Setup**
- [ ] Create `StockMovement` entity and DAO
- [ ] Add migration for new fields in ProductEntity
- [ ] Update `AuroraDatabase` to version 2
- [ ] Write database migration tests

**Days 3-4: Repository Layer**
- [ ] Implement `InventoryRepository`
- [ ] Stock adjustment with audit trail
- [ ] Low stock detection logic
- [ ] Stock history queries
- [ ] Write repository unit tests

**Day 5: ViewModel Foundation**
- [ ] Create `InventoryViewModel`
- [ ] Create `StockAdjustmentViewModel`
- [ ] Implement state management
- [ ] Write ViewModel unit tests

### Week 2: UI Implementation
**Days 1-2: Components**
- [ ] Build reusable inventory components
- [ ] Stock level visualizations
- [ ] Status badges and indicators
- [ ] Movement timeline items

**Days 3-4: Main Screens**
- [ ] `InventoryListScreen` with search/filters
- [ ] `StockAdjustmentScreen` with validation
- [ ] Navigation integration
- [ ] Bottom nav update (3 tabs)

**Day 5: Stock History**
- [ ] `StockHistoryScreen` implementation
- [ ] Timeline view with filtering
- [ ] Link from product detail

### Week 3: Integration & Polish
**Days 1-2: Product Integration**
- [ ] Update `ProductDetailScreen` with stock UI
- [ ] Quick adjustment button
- [ ] Stock history link
- [ ] Low stock threshold settings

**Days 3-4: Testing & Bug Fixes**
- [ ] End-to-end manual testing
- [ ] Fix discovered bugs
- [ ] Write UI/integration tests
- [ ] Performance optimization

**Day 5: Documentation & Release**
- [ ] Create `PHASE_6_SUMMARY.md`
- [ ] Update README with inventory features
- [ ] Take screenshots for documentation
- [ ] Commit, push, and tag release

---

## 🎨 UI/UX Design Specifications

### Inventory List Screen
```
┌─────────────────────────────────┐
│ 🔍 Search inventory...     [≡]  │
├─────────────────────────────────┤
│ [All] [Low Stock] [Out] [Dogs] │ <- Filter chips
├─────────────────────────────────┤
│ ┌─────────────────────────────┐ │
│ │ 🐕 Premium Dog Food         │ │
│ │ SKU: DOG-001                │ │
│ │ 📊 Stock: 45 units          │ │
│ │ [✓ In Stock] $29.99        │ │
│ └─────────────────────────────┘ │
│ ┌─────────────────────────────┐ │
│ │ 🐈 Cat Litter Premium       │ │
│ │ SKU: CAT-015                │ │
│ │ 📊 Stock: 8 units           │ │
│ │ [⚠ Low Stock] $19.99       │ │
│ └─────────────────────────────┘ │
│ ┌─────────────────────────────┐ │
│ │ 🐠 Fish Tank Cleaner        │ │
│ │ SKU: AQU-023                │ │
│ │ 📊 Stock: 0 units           │ │
│ │ [✗ Out of Stock] $12.99    │ │
│ └─────────────────────────────┘ │
└─────────────────────────────────┘
  [Products] [Tasks] [Inventory]
```

### Stock Adjustment Screen
```
┌─────────────────────────────────┐
│ ← Adjust Stock                  │
├─────────────────────────────────┤
│ Premium Dog Food                │
│ Current Stock: 45 units         │
├─────────────────────────────────┤
│ Adjustment Type *               │
│ ( ) Add Stock  ( ) Remove Stock │
│                                 │
│ Quantity *                      │
│ [        10        ]            │
│                                 │
│ Reason *                        │
│ [Received Shipment       ▼]     │
│                                 │
│ Notes (Optional)                │
│ ┌─────────────────────────────┐ │
│ │ Shipment from supplier ABC  │ │
│ │                             │ │
│ └─────────────────────────────┘ │
│                                 │
│ New Stock Level: 55 units       │
│                                 │
│ [Cancel]        [Save Changes]  │
└─────────────────────────────────┘
```

### Stock History Screen
```
┌─────────────────────────────────┐
│ ← Stock History                 │
│   Premium Dog Food              │
├─────────────────────────────────┤
│ Current: 45 units               │
│ Low Stock Threshold: 10         │
├─────────────────────────────────┤
│ ┌─────────────────────────────┐ │
│ │ ● +20 units                 │ │
│ │   Received Shipment         │ │
│ │   Oct 19, 2025 • John       │ │
│ └─────────────────────────────┘ │
│ ┌─────────────────────────────┐ │
│ │ ● -5 units                  │ │
│ │   Sold                      │ │
│ │   Oct 18, 2025 • Sarah      │ │
│ └─────────────────────────────┘ │
│ ┌─────────────────────────────┐ │
│ │ ● -3 units                  │ │
│ │   Damaged                   │ │
│ │   Oct 17, 2025 • Mike       │ │
│ │   "Box torn during delivery"│ │
│ └─────────────────────────────┘ │
└─────────────────────────────────┘
```

---

## 🧪 Testing Strategy

### Unit Tests
- [ ] `InventoryRepository` tests (15+ test cases)
  - Adjust stock with positive/negative quantities
  - Low stock detection
  - Stock history retrieval
  - Edge cases (0 stock, negative attempts)
  
- [ ] `InventoryViewModel` tests (12+ test cases)
  - State management
  - Search and filter logic
  - Low stock alert count
  
- [ ] `StockAdjustmentViewModel` tests (10+ test cases)
  - Validation rules
  - Adjustment submission
  - Error handling

### Integration Tests
- [ ] Database migration test (v1 → v2)
- [ ] Stock movement DAO tests
- [ ] End-to-end stock adjustment flow

### UI Tests (Compose)
- [ ] InventoryListScreen displays products
- [ ] Search filters products correctly
- [ ] Stock status badges show correct states
- [ ] Stock adjustment saves successfully

---

## 🚧 Technical Challenges & Solutions

### Challenge 1: Database Migration
**Problem**: Adding new entity and fields requires migration from v1 to v2  
**Solution**: 
- Create migration script with ALTER TABLE and CREATE TABLE
- Test migration on existing Phase 5 database
- Provide fallback for fresh installs

### Challenge 2: Real-time Stock Updates
**Problem**: Stock changes need to reflect immediately across screens  
**Solution**:
- Use Flow in repository layer
- Observe stock changes in ViewModels
- StateFlow propagates updates to all observers

### Challenge 3: Audit Trail Integrity
**Problem**: Stock movements must be tamper-proof  
**Solution**:
- Foreign key constraints ensure referential integrity
- Timestamps are immutable
- No delete operations on stock movements (append-only log)

### Challenge 4: Low Stock Performance
**Problem**: Calculating low stock across 150+ products on every render  
**Solution**:
- Database index on stockQty field
- Cached Flow with debounce
- Only recalculate when stock changes

---

## 📦 Dependencies

### New Dependencies (None Required)
All necessary dependencies already added in previous phases:
- Room (database) ✅
- Hilt (DI) ✅
- Jetpack Compose (UI) ✅
- Navigation Compose ✅
- Material 3 ✅

### Existing Code to Modify
- `ProductEntity.kt` - Add stock-related fields
- `ProductDao.kt` - Add low stock queries
- `ProductDetailScreen.kt` - Add stock adjustment UI
- `AuroraDatabase.kt` - Bump version to 2
- `MainScreen.kt` - Add Inventory tab (3rd tab)

---

## 🎯 Success Criteria

### Functionality
- ✅ Staff can view all products with current stock levels
- ✅ Staff can add/remove stock with proper audit trail
- ✅ Low stock products are clearly identified
- ✅ Stock history is viewable per product
- ✅ Search and filter work correctly
- ✅ All CRUD operations succeed without crashes

### Code Quality
- ✅ 70%+ test coverage (unit + integration)
- ✅ No lint warnings or errors
- ✅ Consistent architecture (MVVM + Repository)
- ✅ Proper error handling with Result wrapper
- ✅ Clean separation of concerns

### Performance
- ✅ Inventory list loads in <500ms
- ✅ Stock adjustments save instantly
- ✅ Search filters update in real-time
- ✅ No frame drops or UI lag

### User Experience
- ✅ Material 3 design consistency
- ✅ Intuitive navigation and workflows
- ✅ Clear feedback for all actions
- ✅ Helpful empty/error states
- ✅ Accessible color contrasts and touch targets

---

## 🚀 Future Enhancements (Post-Phase 6)

### Phase 7: Reports & Analytics
- Stock value reports
- Movement trends charts
- Category-based analytics
- Export to CSV/PDF

### Phase 8: Advanced Inventory
- Barcode scanning integration
- Supplier management
- Purchase orders
- Expiry date tracking

### Phase 9: Notifications & Automation
- Push notifications for low stock
- Automatic reorder suggestions
- Daily inventory summary emails
- Scheduled stock checks

### Phase 10: Multi-Store Support
- Store-to-store transfers
- Centralized inventory dashboard
- Cross-store stock visibility
- Regional reporting

---

## 📝 Acceptance Checklist

- [ ] All 9 new files created and integrated
- [ ] Database migration successful (v1 → v2)
- [ ] InventoryListScreen displays products with stock levels
- [ ] StockAdjustmentScreen allows add/remove operations
- [ ] Stock movements saved to database with audit trail
- [ ] Low stock products highlighted with badges
- [ ] StockHistoryScreen shows movement timeline
- [ ] ProductDetailScreen updated with stock UI
- [ ] Bottom navigation has 3 tabs (Products, Tasks, Inventory)
- [ ] Search and filters work correctly
- [ ] 70%+ test coverage achieved
- [ ] All tests passing
- [ ] No compilation errors or warnings
- [ ] Manual QA completed (20+ test scenarios)
- [ ] Documentation complete (`PHASE_6_SUMMARY.md`)
- [ ] Screenshots added to README
- [ ] Code committed and pushed to GitHub
- [ ] Git tag created (`v0.6.0-phase6`)

---

## 🎉 Deliverables

1. **Code** (9 new files, 10 modified files, ~2,500 lines)
2. **Tests** (40+ unit tests, 10+ integration tests)
3. **Documentation** (`PHASE_6_SUMMARY.md`, updated README)
4. **Screenshots** (5+ screens documented)
5. **Migration Script** (Room v1 → v2)
6. **Sample Data** (Updated DatabaseSeeder)

---

## 📅 Estimated Timeline

**Total Duration**: 2-3 weeks  
**Effort**: ~60-80 hours  
**Complexity**: Medium-High  

### Breakdown:
- Data Layer: 5 days (40%)
- UI Layer: 7 days (50%)
- Testing & Polish: 3 days (10%)

### Dependencies:
- No external blockers
- All prerequisites met (Phases 1-5 complete)
- Can start immediately

---

**Phase 6 Status**: 📋 **READY TO START**

Once approved, I can begin implementation following this plan. The architecture is proven from Phase 5, and we have all necessary dependencies in place.

**Recommendation**: Complete remaining Phase 5 tasks (tests, README update) before starting Phase 6, or proceed directly if you want to maintain momentum on feature development.
