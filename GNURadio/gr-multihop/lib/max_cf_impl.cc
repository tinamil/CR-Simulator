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
#include "max_cf_impl.h"

namespace gr {
  namespace multihop {

    max_cf::sptr
    max_cf::make(int vec_size)
    {
      return gnuradio::get_initial_sptr
        (new max_cf_impl(vec_size));
    }

    /*
     * The private constructor
     */
    max_cf_impl::max_cf_impl(int vsize)
      : gr::sync_block("max_cf",
              gr::io_signature::make(1, 1, vsize*sizeof(gr_complex)),
              gr::io_signature::make(1, 1, sizeof(int)))
    {
        vec_size = vsize;
    }

    /*
     * Our virtual destructor.
     */
    max_cf_impl::~max_cf_impl()
    {
    }

    int
    max_cf_impl::work(int noutput_items,
			  gr_vector_const_void_star &input_items,
			  gr_vector_void_star &output_items)
    {
        const gr_complex *in = (const gr_complex *) input_items[0];
        int *out = (int *) output_items[0];

        for(int i = 0; i < noutput_items; ++i){
          int start = i * vec_size;
          float max_val = magnitude2(in[start + 1]);
          int max_loc = start;
          for(int j = start; j < start+vec_size; ++j){
            float new_max = magnitude2(in[j]);
            int new_loc = j - (i*vec_size);
            if(new_max > max_val){ 
              max_val = new_max;
              max_loc = new_loc;
            }
          }
          //std::cout << "Max Loc = " << max_loc << std::endl;
          out[i] = max_loc;
        }
        
        // Tell runtime system how many output items we produced.
        return noutput_items;
    }
    
    float max_cf_impl::magnitude2(gr_complex val){
      return val.real()*val.real() + val.imag()*val.imag();
    }

  } /* namespace multihop */
} /* namespace gr */

