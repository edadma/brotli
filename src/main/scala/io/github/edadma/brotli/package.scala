package io.github.edadma.brotli

import io.github.edadma.brotli.extern.LibBrotli as lib

import scala.collection.immutable.ArraySeq
import scala.scalanative.unsafe.*
import scala.scalanative.unsigned.*

enum EncoderMode:
  case GENERIC, TEXT, FONT

enum DecoderResult:
  case ERROR, SUCCESS, NEEDS_MORE_INPUT, NEEDS_MORE_OUTPUT

private def copy(seq: Seq[Byte])(implicit zone: Zone): Ptr[Byte] =
  val buf = alloc[Byte](seq.length.toUInt)
  var i = 0

  while i < seq.length do
    buf(i) = seq(i)
    i += 1

  buf

private def copy(buf: Ptr[Byte], size: Int): Seq[Byte] =
  val arr = new Array[Byte](size)
  var i = 0

  while i < size do
    arr(i) = buf(i)
    i += 1

  arr to ArraySeq

def encoderCompress(quality: Int, lgwin: Int, mode: EncoderMode, input: Seq[Byte]): Option[Seq[Byte]] = Zone {
  implicit z =>
    val encoded_buffer = alloc[Byte]((input.length + 1000).toUInt)
    val encoded_size = stackalloc[CSize]()

    if lib.BrotliEncoderCompress(
        quality,
        lgwin,
        mode.ordinal,
        input.length.toUInt,
        copy(input),
        encoded_size,
        encoded_buffer,
      ) == 0
    then None
    else Some(copy(encoded_buffer, (!encoded_size).toInt))
}
