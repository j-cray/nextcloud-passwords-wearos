
package com.example.nextcloud_passwords_wearos

import android.app.assist.AssistStructure
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillContext
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import com.example.nextcloud_passwords_wearos.data.repository.PasswordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AutofillService : AutofillService(), KoinComponent {

    private val repository: PasswordRepository by inject()
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onFillRequest(request: FillRequest, cancellationSignal: CancellationSignal, callback: FillCallback) {
        val context: List<FillContext> = request.fillContexts
        val structure: AssistStructure = context[context.size - 1].structure

        scope.launch {
            try {
                val passwords = repository.getPasswords()
                val responseBuilder = FillResponse.Builder()

                val focusedViewId = findFocusedView(structure)
                if (focusedViewId != null) {
                    for (password in passwords) {
                        val presentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
                        presentation.setTextViewText(android.R.id.text1, "${password.label} (${password.username})")

                        val datasetBuilder = Dataset.Builder(presentation)
                        
                        // We assume the focused view is the username or password field.
                        // In a real app, we'd try to identify both fields and fill them.
                        // For now, we just fill the focused field with the password value as a fallback
                        // or try to be smart.
                        
                        // Let's just fill the focused view with the password for now, 
                        // or if we can identify it's a username field, fill username.
                        // But identifying fields is hard without heuristics.
                        
                        // Simple approach: Fill the focused field with the password.
                        datasetBuilder.setValue(
                            focusedViewId,
                            AutofillValue.forText(password.password),
                            presentation
                        )
                        
                        responseBuilder.addDataset(datasetBuilder.build())
                    }
                }
                callback.onSuccess(responseBuilder.build())
            } catch (e: Exception) {
                e.printStackTrace()
                callback.onFailure(e.message)
            }
        }
    }

    private fun findFocusedView(structure: AssistStructure): AutofillId? {
        for (i in 0 until structure.windowNodeCount) {
            val windowNode = structure.getWindowNodeAt(i)
            val viewNode = windowNode.rootViewNode
            val focused = findFocusedView(viewNode)
            if (focused != null) return focused
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
    }
}
