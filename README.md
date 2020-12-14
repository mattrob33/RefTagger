# RefTagger
RefTagger is an Android library that transforms Bible references into clickable links, targetting the free BibleGateway API.

## Transform References

```kotlin
val refTagger = RefTagger.Builder().build()

val text = "The reference John 3:16-17 will become a link."
val taggedText = refTagger.tag(text)

aTextView.text = taggedText
```

## Customizations
By default RefTagger requests the NIV version of verses, but this can be customized in the builder.

```kotlin
val refTagger = RefTagger.Builder()
                  .setDefaultVersion("ESV")
                  .build()
```

The way that RefTagger responds to a click is extremely customizable. RefTagger can handle a link in 4 basic ways:

 1. Show an alert dialog with the reference and biblical text (default)
 ```kotlin
val refTagger = RefTagger.Builder()
                  .useDialog() // typically unnecessary, as this is the default
                  .build()
```
 2. Launch Bible Gateway in an external browser.
```kotlin
val refTagger = RefTagger.Builder()
                  .useExternalBrowser()
                  .build()
```
 3. Ignore clicks.
  ```kotlin
val refTagger = RefTagger.Builder()
                  .ignoreClicks()
                  .build()
```
 4. Custom click handler (more below)

```kotlin
val refTagger = RefTagger.Builder()
                  .setClickHandler(...)
                  .build()
```

Note that only one click handling mechanism is allowed. If multiple mechanisms are provided, RefTagger will use the last one provided and ignore the others.

So in the code below, RefTagger will ignore clicks and *will not* launch an external browser or a dialog.

```kotlin
val refTagger = RefTagger.Builder()
                  .useExternalBrowser()
                  .useDialog()
                  .ignoreClicks()
                  .build()
```

## Click Handlers
RefTagger accepts a few different custom click handlers to provide more control over the way that the text is displayed.

 1. BibleGatewayURL
 
This handler returns the Bible Gateway URL for the reference the user clicked on. This could be useful as an alternative to `Builder.useExternalBrowser()` if you want to display Bible Gateway in a custom WebView within your app, rather than launching an external browser.
 ```kotlin
val refTagger = RefTagger.Builder()
                  .setClickHandler(object : ClickHandler.BibleGatewayURL() {
                    override fun onClick(url: String) {
                      // do something with the URL
                    }
                  })
                  .build()
```
 2. ScriptureReference
 
This handler returns the exact Scripture reference that the user clicked on (e.g., "John 3:16-17"). This could be useful if you want to target an API other than Bible Gateway, or if you are using RefTagger within an app that already has a biblical text stored locally.
 ```kotlin
val refTagger = RefTagger.Builder()
                  .setClickHandler(object : ClickHandler.ScriptureReference() {
                    override fun onClick(reference: String) {
                      // do something with the scripture reference
                    }
                  })
                  .build()
```
 3. ScriptureText
 
 This handler returns the actual biblical text (e.g., "For God so loved the world...") for the Scripture reference that the user clicked on (e.g., "John 3:16-17").
 
 Like `Builder.useDialog()` this approach places the burden of the network request (and threading, stream reading, error handling) on RefTagger so you don't have to worry about it. However, this approach also provides more flexibility in UI/UX since the UI/UX implementation is left up to you. This could be useful if you want to display the response within a BottomSheetDialog or some other view within your app's UI.
 
 ```kotlin
val refTagger = RefTagger.Builder()
                  .setClickHandler(object : ClickHandler.ScriptureText() {
                    override fun onSuccess(ref: String, text: String) {
                      // do something with the text and reference
                    }

                    override fun onError(message: String) {
                      // handle the error
                    }
                   })
                  .build()
```