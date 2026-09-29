# Remove Categories and Condition Features

Simplify the MarketHub app by removing categories and product condition fields, filters, database columns, and UI components.

## User Review Required

> [!IMPORTANT]
> Removing database columns (`category` and `condition`) will alter the Room database schema. We will increment the database version in `AppDatabase.java` to handle schema migration/recreation.

## Proposed Changes

### Database & Repository
#### [MODIFY] [Product.java](file:///C:/Users/Shourya/AndroidStudioProjects/MarketHub/app/src/main/java/com/example/markethub/database/Product.java)
- Remove `category` and `condition` fields, getters, setters, and constructors parameters.

#### [MODIFY] [ProductDao.java](file:///C:/Users/Shourya/AndroidStudioProjects/MarketHub/app/src/main/java/com/example/markethub/database/ProductDao.java)
- Update `filterProducts` query to remove category and condition parameters and filtering logic.

#### [MODIFY] [AppDatabase.java](file:///C:/Users/Shourya/AndroidStudioProjects/MarketHub/app/src/main/java/com/example/markethub/database/AppDatabase.java)
- Increment database version (e.g. from 1 to 2) with fallbackToDestructiveMigration().

#### [MODIFY] [ProductRepository.java](file:///C:/Users/Shourya/AndroidStudioProjects/MarketHub/app/src/main/java/com/example/markethub/repository/ProductRepository.java)
- Remove category mapping (`mapCategory`) and pass-through category/condition logic.

### Adapters & UI
#### [DELETE] [CategoryAdapter.java](file:///C:/Users/Shourya/AndroidStudioProjects/MarketHub/app/src/main/java/com/example/markethub/adapter/CategoryAdapter.java)
- Delete category adapter class.

#### [DELETE] [item_category_chip.xml](file:///C:/Users/Shourya/AndroidStudioProjects/MarketHub/app/src/main/res/layout/item_category_chip.xml)
- Delete layout file for category chips.

#### [MODIFY] [ProductAdapter.java](file:///C:/Users/Shourya/AndroidStudioProjects/MarketHub/app/src/main/java/com/example/markethub/adapter/ProductAdapter.java)
- Remove references to `tvCategoryBadge` and `tvConditionBadge`.

#### [MODIFY] [item_product_card.xml](file:///C:/Users/Shourya/AndroidStudioProjects/MarketHub/app/src/main/res/layout/item_product_card.xml)
- Remove category and condition badge TextViews.

#### [MODIFY] [MainActivity.java](file:///C:/Users/Shourya/AndroidStudioProjects/MarketHub/app/src/main/java/com/example/markethub/ui/MainActivity.java)
- Remove `CategoryAdapter`, category RecyclerView setup, condition filter logic, and filter dialog condition spinner.

#### [MODIFY] [activity_main.xml](file:///C:/Users/Shourya/AndroidStudioProjects/MarketHub/app/src/main/res/layout/activity_main.xml)
- Remove category RecyclerView from layout.

#### [MODIFY] [dialog_filter.xml](file:///C:/Users/Shourya/AndroidStudioProjects/MarketHub/app/src/main/res/layout/dialog_filter.xml)
- Remove condition filter spinner section.

#### [MODIFY] [AddEditProductActivity.java](file:///C:/Users/Shourya/AndroidStudioProjects/MarketHub/app/src/main/java/com/example/markethub/ui/AddEditProductActivity.java)
- Remove category and condition Spinners and their handling.

#### [MODIFY] [activity_add_edit_product.xml](file:///C:/Users/Shourya/AndroidStudioProjects/MarketHub/app/src/main/res/layout/activity_add_edit_product.xml)
- Remove category and condition Spinner views.

#### [MODIFY] [ProductDetailActivity.java](file:///C:/Users/Shourya/AndroidStudioProjects/MarketHub/app/src/main/java/com/example/markethub/ui/ProductDetailActivity.java)
- Remove category and condition TextView displays.

#### [MODIFY] [activity_product_detail.xml](file:///C:/Users/Shourya/AndroidStudioProjects/MarketHub/app/src/main/res/layout/activity_product_detail.xml)
- Remove category and condition TextViews.

#### [MODIFY] [Constants.java](file:///C:/Users/Shourya/AndroidStudioProjects/MarketHub/app/src/main/java/com/example/markethub/utils/Constants.java)
- Remove `CATEGORIES`, `FORM_CATEGORIES`, `CONDITIONS`, `FORM_CONDITIONS`.

## Verification Plan

### Automated Tests
- Run `:app:assembleDebug` and `:app:lintDebug` to ensure compilation and lint checks pass.
