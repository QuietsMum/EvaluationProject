package com.example.evaluationproject.ui.preview

import android.graphics.drawable.Drawable

open class EvaPreviewModal
class FilesItem(val name: String, val image: Drawable) : EvaPreviewModal()
class AudioItem(val name: String, val audio: String) : EvaPreviewModal()
class EmptyItem() : EvaPreviewModal()