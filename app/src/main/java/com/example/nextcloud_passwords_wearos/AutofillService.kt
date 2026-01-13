
package com.example.nextcloud_passwords_wearos

import android.app.assist.AssistStructure
import android.content.IntentSender
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.FillCallback
import android.service.autofill.FillContext
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import androidx.autofill.inline.v1.InlineSuggestionUi
import java.util.regex.Pattern

class AutofillService : AutofillService() {

    override fun onFillRequest(request: FillRequest, cancellationSignal: CancellationSignal, callback: FillCallback) {
        val context: List<FillContext> = request.fillContexts
        val structure: AssistStructure = context[context.size - 1].structure

        val responseBuilder = FillResponse.Builder()

        // Dummy data - in a real app, you'd fetch this from your password manager
        val dummyUsername = "testuser"
        val dummyPassword = "password123"

        val focusedViewId = findFocusedView(structure)
        if (focusedViewId != null) {
            val usernamePresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
            usernamePresentation.setTextViewText(android.R.id.text1, "Username: $dummyUsername")

            val passwordPresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
            passwordPresentation.setTextViewText(android.R.id.text1, "Password")

            responseBuilder
                .addDataset(
                    android.service.autofill.Dataset.Builder(usernamePresentation)
                        .setValue(
                            focusedViewId,
                            AutofillValue.forText(dummyUsername),
                            usernamePresentation
                        )
                        .build()
                )
                .addDataset(
                    android.service.autofill.Dataset.Builder(passwordPresentation)
                        .setValue(
                            focusedViewId,
                            AutofillValue.forText(dummyPassword),
                            passwordPresentation
                        )
                        .build()
                )
        }

        callback.onSuccess(responseBuilder.build())
    }

    private fun findFocusedView(structure: AssistStructure): AutofillId? {
        for (i in 0 until structure.windowNodeCount) {
            val windowNode = structure.getWindowNodeAt(i)
            val viewNode = windowNode.rootViewNode
            return findFocusedView(viewNode)
        }
        return null
    }

    private fun findFocusedView(viewNode: AssistStructure.ViewNode): AutofillId? {
        if (viewNode.isFocused) {
            return viewNode.autofillId
        }
        for (i in 0 until viewNode.childCount) {
            val childNode = viewNode.getChildAt(i)
            val focusedId = findFocusedView(childNode)
            if (focusedId != null) {
                return focusedId
            }
        }
        return null
    }


    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        // TODO: Implement the save logic here.
        // This method is called when the user wants to save the autofill data.
    }
}
