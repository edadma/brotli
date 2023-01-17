package io.github.edadma.brotli

import io.github.edadma.brotli.extern.LibBrotli as lib

import scala.collection.immutable.ArraySeq
import scala.scalanative.unsafe.*
import scala.scalanative.unsigned.*

enum EncoderMode:
  case GENERIC, TEXT, FONT

enum EncoderParameter:
  case MODE, QUALITY, LGWIN, LGBLOCK, DISABLE_LITERAL_CONTEXT_MODELING, SIZE_HINT, LARGE_WINDOW, NPOSTFIX, NDIRECT,
    STREAM_OFFSET

//enum DecoderResult:
//  case ERROR, SUCCESS, NEEDS_MORE_INPUT, NEEDS_MORE_OUTPUT

val DEFAULT_QUALITY = 11

val DEFAULT_WINDOW = 22

private val ERROR = 0

private def copy(seq: IndexedSeq[Byte])(implicit zone: Zone): Ptr[Byte] =
  val buf = alloc[Byte](seq.length.toUInt)
  var i = 0

  while i < seq.length do
    buf(i) = seq(i)
    i += 1

  buf

private def copy(buf: Ptr[Byte], size: Int): IndexedSeq[Byte] =
  val arr = new Array[Byte](size)
  var i = 0

  while i < size do
    arr(i) = buf(i)
    i += 1

  arr to ArraySeq

def encoderVersion: Int = lib.BrotliEncoderVersion
def encoderCompress(quality: Int, lgwin: Int, mode: EncoderMode, input: IndexedSeq[Byte]): Option[IndexedSeq[Byte]] =
  Zone { implicit z =>
    val size = lib.BrotliEncoderMaxCompressedSize(input.length.toULong)
    val encoded_buffer = alloc[Byte](size)
    val encoded_size = stackalloc[CSize]()

    !encoded_size = size

    if lib.BrotliEncoderCompress(
        quality,
        lgwin,
        mode.ordinal,
        input.length.toUInt,
        copy(input),
        encoded_size,
        encoded_buffer,
      ) == ERROR
    then None
    else Some(copy(encoded_buffer, (!encoded_size).toInt))
  }

implicit class EncoderState(val stateptr: lib.encoderState_tp):
  def destroyInstance(): Unit = lib.BrotliEncoderDestroyInstance(stateptr)
  def hasMoreOutput: Boolean = lib.BrotliEncoderHasMoreOutput(stateptr) != 0
  def isFinished: Boolean = lib.BrotliEncoderIsFinished(stateptr) != 0
  def setParameter(param: EncoderParameter, value: Int): Boolean =
    lib.BrotliEncoderSetParameter(stateptr, param.ordinal, value) != 0
  def takeOutput(max: Int): IndexedSeq[Byte] =
    val size = stackalloc[CSize]()

    !size = max.toUInt
    copy(lib.BrotliEncoderTakeOutput(stateptr, size), (!size).toInt)

def encoderCreateInstance: EncoderState = lib.BrotliEncoderCreateInstance(null, null, null)
def encoderMaxCompressedSize(input_size: Long): Long = lib.BrotliEncoderMaxCompressedSize(input_size.toULong).toLong

def decoderDecompress(encoded: IndexedSeq[Byte]): Option[IndexedSeq[Byte]] = Zone { implicit z =>
  val size = (encoded.length * 7).toUInt
  val decoded_buffer = alloc[Byte](size)
  val decoded_size = stackalloc[CSize]()

  !decoded_size = size

  if lib.BrotliDecoderDecompress(encoded.length.toUInt, copy(encoded), decoded_size, decoded_buffer) == ERROR then None
  else Some(copy(decoded_buffer, (!decoded_size).toInt))
}
