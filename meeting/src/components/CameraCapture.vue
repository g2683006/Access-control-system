<template>
  <div class="camera">
    <div class="preview">
      <video v-show="streaming && !photoUrl" ref="video" autoplay playsinline muted></video>
      <img v-if="photoUrl" :src="photoUrl" alt="照片">
      <div v-if="!streaming && !photoUrl" class="placeholder">
        <i class="el-icon-camera"></i>
        <p>{{ cameraError || '打开摄像头拍照，或者直接上传一张正脸照片' }}</p>
      </div>
    </div>
    <div class="actions">
      <el-button v-if="!streaming" icon="el-icon-video-camera" @click="open">打开摄像头</el-button>
      <el-button v-else type="success" icon="el-icon-camera" @click="snap">拍照</el-button>
      <el-button v-if="streaming" icon="el-icon-switch-button" @click="close">关闭摄像头</el-button>
      <el-button icon="el-icon-refresh" @click="reset">重置</el-button>
      <el-button icon="el-icon-picture-outline" @click="$refs.file.click()">图片上传</el-button>
      <slot></slot>
      <input ref="file" type="file" accept="image/*" style="display: none" @change="onFile">
    </div>
  </div>
</template>

<script>
// 拍照/选图组件：拿到照片后通过 change 事件把 File 交给父组件
export default {
  name: 'CameraCapture',
  data() {
    return {
      streaming: false,
      stream: null,
      photoUrl: '',
      cameraError: ''
    }
  },
  beforeDestroy() {
    this.close()
    this.revoke()
  },
  methods: {
    async open() {
      this.cameraError = ''
      if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        this.cameraError = '浏览器不能使用摄像头（只有 localhost 或 https 页面可以用），请改用图片上传'
        return
      }
      try {
        this.reset()
        this.stream = await navigator.mediaDevices.getUserMedia({video: {width: 640, height: 480}})
        this.$refs.video.srcObject = this.stream
        this.streaming = true
      } catch (e) {
        this.cameraError = '打开摄像头失败（' + e.message + '），请改用图片上传'
      }
    },
    snap() {
      const video = this.$refs.video
      const canvas = document.createElement('canvas')
      canvas.width = video.videoWidth || 640
      canvas.height = video.videoHeight || 480
      canvas.getContext('2d').drawImage(video, 0, 0, canvas.width, canvas.height)
      canvas.toBlob(blob => {
        this.setPhoto(new File([blob], 'camera.jpg', {type: 'image/jpeg'}))
        this.close()
      }, 'image/jpeg', 0.9)
    },
    close() {
      if (this.stream) {
        this.stream.getTracks().forEach(track => track.stop())
      }
      this.stream = null
      this.streaming = false
    },
    reset() {
      this.revoke()
      this.photoUrl = ''
      this.$emit('change', null)
    },
    onFile(e) {
      const file = e.target.files[0]
      e.target.value = ''
      if (!file) {
        return
      }
      if (!file.type.startsWith('image/')) {
        this.$message.error('请选择图片文件')
        return
      }
      if (file.size > 10 * 1024 * 1024) {
        this.$message.error('图片不能超过 10MB')
        return
      }
      this.close()
      this.setPhoto(file)
    },
    setPhoto(file) {
      this.revoke()
      this.photoUrl = URL.createObjectURL(file)
      this.$emit('change', file)
    },
    revoke() {
      if (this.photoUrl) {
        URL.revokeObjectURL(this.photoUrl)
      }
    }
  }
}
</script>

<style scoped>
.preview {
  width: 100%;
  max-width: 480px;
  height: 360px;
  margin: 0 auto;
  background: #f5f7fa;
  border: 1px dashed #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.preview video, .preview img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.placeholder {
  text-align: center;
  color: #909399;
  padding: 0 20px;
}

.placeholder i {
  font-size: 48px;
  margin-bottom: 8px;
}

.actions {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
}

.actions .el-button + .el-button {
  margin-left: 0;
}
</style>
