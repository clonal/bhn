package utils

import java.io.File

import play.api.Logger

object FileUtil {

  def deleteFile(file: File) = {
    if (file.exists()) {
      file.delete()
      Logger.debug(s"删除文件: ${file.getName}")
    }
  }
}
