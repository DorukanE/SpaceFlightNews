package com.dorukaneskiceri.spaceflightnews.util.ext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun CoroutineScope.runWithIO(block: suspend CoroutineScope.() -> Unit) =
    launch(Dispatchers.IO) { block() }

fun CoroutineScope.runWithMain(block: suspend CoroutineScope.() -> Unit) =
    launch(Dispatchers.Main) { block() }
