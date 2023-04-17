/*
 * Copyright (C) 2017, hapjs.org. All rights reserved.
 */
package org.hapjs.bridge

interface Extension {
    /**
     * Invocation mode.
     */
    enum class Mode {
        /**
         * Synchronous invocation. When calling actions in such mode, caller
         * will get response until invocation finished.
         */
        SYNC,

        /**
         * Asynchronous invocation. When calling actions in such mode, caller
         * will get an empty response immediately, but wait in a different
         * thread to get response until invocation finished.
         */
        ASYNC,

        /**
         * Callback invocation. When calling actions in such mode, caller will
         * get an empty response immediately, but receive response through
         * callback when invocation finished.
         */
        CALLBACK,

        /**
         * Synchronous invocation. When calling actions in such mode, caller
         * will get response until invocation finished, but receive response
         * through callback later.
         */
        SYNC_CALLBACK
    }

    enum class Type {
        FUNCTION, ATTRIBUTE, EVENT
    }

    enum class Access {
        NONE, READ, WRITE
    }

    enum class Normalize {
        RAW, JSON
    }

    enum class NativeType {
        INSTANCE
    }

    enum class Multiple {
        SINGLE, MULTI
    }

    companion object {
        const val ACTION_INIT = "__init__"
    }
}