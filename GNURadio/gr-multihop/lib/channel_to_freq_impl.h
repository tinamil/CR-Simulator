/* -*- c++ -*- */
/* 
 * Copyright 2015 <+YOU OR YOUR COMPANY+>.
 * 
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this software; see the file COPYING.  If not, write to
 * the Free Software Foundation, Inc., 51 Franklin Street,
 * Boston, MA 02110-1301, USA.
 */

#ifndef INCLUDED_MULTIHOP_CHANNEL_TO_FREQ_IMPL_H
#define INCLUDED_MULTIHOP_CHANNEL_TO_FREQ_IMPL_H

#include <multihop/channel_to_freq.h>

namespace gr {
  namespace multihop {

    class channel_to_freq_impl : public channel_to_freq
    {
     private:
      double fft_bin_size;
      double fft_channel_ratio;
      int round;

     public:
      channel_to_freq_impl(int fft_size, double sample_rate, int num_channels);
      ~channel_to_freq_impl();

      // Where all the action really happens
      int work(int noutput_items,
	       gr_vector_const_void_star &input_items,
	       gr_vector_void_star &output_items);
    };

  } // namespace multihop
} // namespace gr

#endif /* INCLUDED_MULTIHOP_CHANNEL_TO_FREQ_IMPL_H */

