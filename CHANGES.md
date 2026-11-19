# MAS Cake – update 19.11

Your previous version is saved in git as the commit "commit before new update 19.11".
After pulling these files: **File → Sync Project with Gradle Files**, then run the app.

## New dependencies (gradle/libs.versions.toml)
- `lottie-compose` 6.6.2 – Lottie animations (splash + success)
- `work-runtime-ktx` 2.10.0 – local notification after placing an order
- Updated: core-ktx 1.15.0, lifecycle 2.8.7, activity-compose 1.9.3, coil 2.7.0
- Manifest: added `POST_NOTIFICATIONS` permission (asked once when the customer enters the shop)

## New structure
```
data/Models.kt          Cake, Order, CartItem, AppSettings, enums, formatRub()
data/CakeCatalog.kt     the ONE list of all cakes (prices + descriptions)
data/AppStore.kt        shared state for customer app + admin panel, saved with SharedPreferences
notifications/OrderNotificationWorker.kt
ui/components/Components.kt   cards, shimmer, Lottie, empty-plate illustration, price breakdown…
screens/…               customer screens
screens/admin/…         admin panel
res/raw/*.json          Lottie animations
```

## Admin panel (Role screen → "I run the bakery")
1. Dashboard: today's revenue  2. order counters  3. low / out-of-stock alerts  4. quick actions
5. Live order list  6. status button New → Preparing → Out for delivery → Completed
7. Order details (address, phone with Call button, special request, items, cancel)
8. History with filters (Delivered / Cancelled, Today / 7 days / All time)
9. All cakes list  10. availability switch  11. Add / edit cake form with photo upload
12. Promo manager (on/off, 5–50 % slider, choose which cakes are on promo)
13. Customer list with "send SMS" button  14. Analytics (best sellers, 7-day revenue)
15. Settings: working hours, delivery fee, free-delivery threshold, contact phone

Everything the admin changes shows up in the customer app right away (prices, stock,
discount %, delivery fee, hours). Orders placed by customers appear in the admin panel.

## Customer app
1. Auto-scrolling promo carousel (HorizontalPager)  2. 1-second shimmer on category & search
3. Lottie cake on splash, Lottie checkmark on success  4. Bottom sheet to edit cart items
5. "Added to cart ✔" snackbar (you stay on the page)  6. Subtotal / Delivery / Discount / Total
7. Empty-plate illustration  8. ♡ Favorites on every cake + Favorites page in Profile
9. Recently viewed (last 3) on Home  10. Edit profile (saved locally)
11. Notification 10 s after ordering: "Your order #1042 is being prepared!"
12. Animated tracking bar on the success screen  13. Dark mode switch (Profile → Settings)
14. Filter chips: Under 2000 ₽ / Chocolate / Fruit / Bestsellers  15. Sort by price
16. Recent searches (last 3) under the search bar

## Bugs fixed
- The cake list was copied in 5 files with different prices → now one catalog.
- Promo items were added as "1 kg" (English) but the cart expected "1 кг" → weight chips broke and the discount was lost when changing weight. Weights are numbers now; the discount is calculated in the cart.
- Delivery fee was shown ("from 300 ₽") but never added to the total.
- Pressing the phone's Back button closed the app → there is now a proper back stack.
- Coming back from a cake page always reset the bottom tab to "Cakes".
- "Promos" tab showed "Loading Promos..." forever and "Menu" was an empty placeholder → both are real screens now.
- The app theme (`MAS_CAKE1Theme`) was never applied.
- The language switch only worked on some screens (splash, success, profile, category prices were always Russian); it is now one global setting that is remembered.
- Search only worked by tapping the icon → keyboard "Search" works, and Russian names are searchable too.
- Bento (sold per piece) showed weight options on its detail page.
- Checkout now asks for name/phone (pre-filled from the profile) and has a "special requests" field (e.g. "Write 'Happy Birthday'").
- Register screen validates name/phone/password.
- Admin button on the role screen did nothing.
- App name in the launcher: "MAS_Cake1" → "MAS Cake".

## Notes
- Data is stored on the phone (SharedPreferences). To reset the demo data, clear the app's storage.
- The app starts with some sample orders so the dashboard and charts aren't empty.
