package com.example.mytasker.api;

import com.example.mytasker.models.Message;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;

public class API {
    private static FirebaseFunctions mFunctions;

    public API() {
        mFunctions = FirebaseFunctions.getInstance();
    }

    public Task<Message> createTask(Map data) {
        // Create the arguments to the callable function.
        return mFunctions
                .getHttpsCallable("createTask")
                .call(data)
                .continueWith(task -> {
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.
                    Message message = new Message((HashMap) task.getResult().getData());
                    return message;
                });
    }
}
