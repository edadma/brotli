package io.github.edadma.brotli.extern

import scala.scalanative.unsafe._

@link("brotlienc")
@extern
object LibBrotli:
  def BrotliEncoderCompress(
      quality: CInt,
      lgwin: CInt,
      mode: CInt,
      input_size: CSize,
      input_buffer: Ptr[Byte],
      encoded_size: Ptr[CSize],
      encoded_buffer: Ptr[Byte],
  ): CInt = extern
  def BrotliDecoderDecompress(
      encoded_size: CSize,
      encoded_buffer: Ptr[Byte],
      decoded_size: Ptr[CSize],
      decoded_buffer: Ptr[Byte],
  ): CInt = extern
