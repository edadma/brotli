package io.github.edadma.libbrotli.extern

import scala.scalanative.unsafe._

@link("brotlienc")
@link("brotlidec")
@extern
object LibBrotli:
  type encoderState_t = CStruct0
  type encoderState_tp = Ptr[encoderState_t]

  // compression

  def BrotliEncoderCompress(
      quality: CInt,
      lgwin: CInt,
      mode: CInt,
      input_size: CSize,
      input_buffer: Ptr[Byte],
      encoded_size: Ptr[CSize],
      encoded_buffer: Ptr[Byte],
  ): CInt = extern
  def BrotliEncoderCreateInstance(alloc_func: Ptr[Byte], free_func: Ptr[Byte], opaque: Ptr[Byte]): encoderState_tp =
    extern
  def BrotliEncoderDestroyInstance(state: encoderState_tp): Unit = extern
  def BrotliEncoderHasMoreOutput(state: encoderState_tp): CInt = extern
  def BrotliEncoderIsFinished(state: encoderState_tp): CInt = extern
  def BrotliEncoderMaxCompressedSize(input_size: CSize): CSize = extern
  def BrotliEncoderSetParameter(state: encoderState_tp, param: CInt, value: CInt): CInt = extern
  def BrotliEncoderTakeOutput(state: encoderState_tp, size: Ptr[CSize]): Ptr[Byte] = extern
  def BrotliEncoderCompressStream(
      state: encoderState_tp,
      op: CInt,
      available_in: Ptr[CSize],
      next_in: Ptr[Ptr[Byte]],
      available_out: Ptr[CSize],
      next_out: Ptr[Ptr[Byte]],
      total_out: Ptr[CSize],
  ): CInt = extern
  def BrotliEncoderVersion: CInt = extern

  // decompression

  def BrotliDecoderDecompress(
      encoded_size: CSize,
      encoded_buffer: Ptr[Byte],
      decoded_size: Ptr[CSize],
      decoded_buffer: Ptr[Byte],
  ): CInt = extern
