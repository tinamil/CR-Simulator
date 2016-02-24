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
#include "mode_ii_impl.h"

namespace gr {
  namespace multihop {

    mode_ii::sptr
    mode_ii::make(int size)
    {
      return gnuradio::get_initial_sptr
        (new mode_ii_impl(size));
    }

    /*
     * The private constructor
     */
    mode_ii_impl::mode_ii_impl(int size)
      : gr::sync_decimator("mode_ii",
              gr::io_signature::make(1, 1, sizeof(int)),
              gr::io_signature::make(1, 1, sizeof(int)), size),
              i_size(size)
    {}

    /*
     * Our virtual destructor.
     */
    mode_ii_impl::~mode_ii_impl()
    {
    }

    int
    mode_ii_impl::work(int noutput_items,
			  gr_vector_const_void_star &input_items,
			  gr_vector_void_star &output_items)
    {
        const int *in = (const int *) input_items[0];
        int *out = (int *) output_items[0];
        
        for(int i = 0; i < noutput_items; ++i){
          int start = i * i_size;
          std::map<int, int> map;
        
          for(int j = start; j < start + i_size; ++j){
            if(map.count(in[j]) == 0) map[in[j]] = 1;
            else map[in[j]] = map[in[j]] + 1;
          }
        
          int max_item = -1;
          int max_mode = -1;
          for (std::map<int,int>::iterator it = map.begin(); it != map.end(); ++it) {
            //std::cout << "Max_mode values: " << it->first << " = " << it->second << std::endl;
            if(it->second > max_mode){
              max_mode = it->second;
              max_item = it->first;
            }
          }
          out[i] = max_item;
          //std::cout << "Max_mode: " << max_item << " = " << max_mode << std::endl;
        }
        // Tell runtime system how many output items we produced.
        return noutput_items;
    }

  } /* namespace multihop */
} /* namespace gr */

