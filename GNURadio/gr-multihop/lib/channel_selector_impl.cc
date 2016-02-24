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
#include "channel_selector_impl.h"

namespace gr {
  namespace multihop {

    channel_selector::sptr
    channel_selector::make(int fft)
    {
      return gnuradio::get_initial_sptr
        (new channel_selector_impl(fft));
    }

    /*
     * The private constructor
     */
    channel_selector_impl::channel_selector_impl(int num_channels) :
	    gr::sync_block("channel_selector", 
	    gr::io_signature::make(1, 1, sizeof(int)), 
	    gr::io_signature::make(1, 1, sizeof(int)))
	  {
	    for(int i = 0; i < num_channels; ++i){
        channels.push_back(i);
      }
    }

    /*
     * Our virtual destructor.
     */
    channel_selector_impl::~channel_selector_impl(){
    
    }

    int
    channel_selector_impl::work(int noutput_items,
			  gr_vector_const_void_star &input_items,
			  gr_vector_void_star &output_items)
    {
        const int *in = (const int *) input_items[0];
        int *out = (int *) output_items[0];
      
        for(int i = 0; i < noutput_items; ++i){
          out[i] = channels[in[i] % channels.size()];
        }
        
        // Tell runtime system how many input items we consumed on
        // each input stream.
        consume_each (noutput_items);

        // Tell runtime system how many output items we produced.
        return noutput_items;
    }

  } /* namespace multihop */
} /* namespace gr */

