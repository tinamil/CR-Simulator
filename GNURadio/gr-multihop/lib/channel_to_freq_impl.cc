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

#ifdef HAVE_CONFIG_H
#include "config.h"
#endif

#include <gnuradio/io_signature.h>
#include "channel_to_freq_impl.h"

namespace gr {
  namespace multihop {

    channel_to_freq::sptr
    channel_to_freq::make(int fft_size, double sample_rate, int num_channels)
    {
      return gnuradio::get_initial_sptr
      (new channel_to_freq_impl(fft_size, sample_rate, num_channels));
    }

    /*
     * The private constructor
     */
    channel_to_freq_impl::channel_to_freq_impl(int fft_size, double sample_rate, int num_channels)
      : gr::sync_block("channel_to_freq",
              gr::io_signature::make(1, 1, sizeof(int)),
              gr::io_signature::make(1, 1, sizeof(float)))
    {
      round = 0;
      fft_bin_size = sample_rate / fft_size;
      fft_channel_ratio = (fft_size / 2.0) / (double)num_channels;
      std::cout << "fft_channel_ratio = " << fft_channel_ratio << std::endl;
    }

    /*
     * Our virtual destructor.
     */
    channel_to_freq_impl::~channel_to_freq_impl()
    {
    }

    int
    channel_to_freq_impl::work(int noutput_items,
			  gr_vector_const_void_star &input_items,
			  gr_vector_void_star &output_items)
    {
        const int *in = (const int *) input_items[0];
        float *out = (float *) output_items[0];
        
        for(int i = 0; i < noutput_items; ++i){
          int fft_bin = (in[i] * fft_channel_ratio + fft_channel_ratio * 0.5);
          out[i] = fft_bin * fft_bin_size; //Convert to frequency
          std::cout << "Round " << round++ << " ";
          std::cout << "Channel: " << in[i] << " & Bin: " << fft_bin << " & Freq: " << out[i] << std::endl;
        }

        return noutput_items;
    }

  } /* namespace multihop */
} /* namespace gr */

