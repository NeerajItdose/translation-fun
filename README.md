# translation fun
This project is an example application to translate simple English phrases to German and uses common Android features.
* MVVW architecture
  * ModelView
  * Repository
  * LiveData
* Persistence via ROOM database framework
* Multithreading and background tasks
* Networking using Google Translation Api
* Testing
  * JUnit
  * ROOM
  * UI
* Design
  * Card layout
  * Custom AutoCompleteTextView
  * Custom RecyclerView SwipeAction to delete cards
  
Checkout the related projects
* translation-fun realized with Kotlin
* translation-fun realized with JavaRX
  
To use the Google Translation Api you have to optain an ApiKey from Google Cloud Console. Using the Translation Api **is not free**, but the price for a single translation is very low. Anyway be aware of that!! You find more information here -> https://cloud.google.com/translate/pricing
* Register for Google Cloud Console
* Search for the Translation Api
* Activate the Api. You have to create or select an purchase account, where you setup your payment method.
* Create an ApiKey for the Translation Api. You can restrict your key to the Translation Api only.
* Create the following resource xml file with any name inside the app project under **app/res/values** and enter your api key.

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
  <string name="api_key">...ENTER YOUR API KEY HERE...</string>
</resources>
```
