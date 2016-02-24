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
#include "freqhop_signal_source_impl.h"

namespace gr {
  namespace multihop {

    freqhop_signal_source::sptr
    freqhop_signal_source::make(double sampling_freq, double ampl, gr_complex offset, double freqhop_rate)
    {
      return gnuradio::get_initial_sptr
        (new freqhop_signal_source_impl(sampling_freq, ampl, offset, freqhop_rate));
    }

    /*
     * The private constructor
     */
    freqhop_signal_source_impl::freqhop_signal_source_impl(double sampling_freq, double ampl, gr_complex offset, double freqhop_rate)
      : gr::sync_interpolator("freqhop_signal_source",
              gr::io_signature::make(1, 1, sizeof(float)),
              gr::io_signature::make(1, 1, sizeof(gr_complex)), 
              round(sampling_freq / freqhop_rate)),
      d_sampling_freq(sampling_freq), d_ampl(ampl), d_offset(offset)
    {
      time = 0;
      interp_rate = round(sampling_freq / freqhop_rate);
    }

    /*
     * Our virtual destructor.
     */
    freqhop_signal_source_impl::~freqhop_signal_source_impl()
    {
    }

    int
    freqhop_signal_source_impl::work(int num_output_items,
			  gr_vector_const_void_star &input_items,
			  gr_vector_void_star &output_items)
    {
      const float *in = (const float *) input_items[0];
      gr_complex *optr = (gr_complex*)  output_items[0];
      int ninput_items = num_output_items / interp_rate;
      for(int i = 0; i < ninput_items; ++i){
        float freq = in[i];
       // std::cout << "Freq: " << freq << std::endl;
        for(int j = i; j < (i+1) * interp_rate; ++j){
          double val = 2 * D_PI * freq * time / d_sampling_freq;
          time += 1;
          optr[j] = gr_complex(d_ampl*cos(val), d_ampl*sin(val));
	        if(d_offset != gr_complex(0,0)){
	          optr[j] += d_offset;  
	        }
        }
      }
      return num_output_items;
    }

 

  } /* namespace multihop */
} /* namespace gr */

