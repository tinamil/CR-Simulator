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
#include "multihop_rendezvous_impl.h"

namespace gr {
  namespace multihop { 

    multihop_rendezvous::sptr
    multihop_rendezvous::make(int seed, double freq_rate, double samp_rate, int num_channels, int window_size, int search_speed)
    {
      return gnuradio::get_initial_sptr
        (new multihop_rendezvous_impl(seed, freq_rate, samp_rate, num_channels, window_size, search_speed));
    }

    /*
     * The private constructor
     */
    multihop_rendezvous_impl::multihop_rendezvous_impl(int seed, double freq_rate, double samp_rate, int nchannels, int window_size, int search_speed)
      : gr::sync_block("multihop_rendezvous",
              gr::io_signature::make(1, 1, sizeof(int)),
              gr::io_signature::make(0, 0, 0))
    {
      max_window_size = window_size;
      sliding_window = std::vector<int>(max_window_size);
      rng = new csprng_impl(seed, freq_rate, samp_rate);
      num_channels = nchannels;
      search_rate = search_speed;
   
      sliding_index = 0;
  		last_window_update = 0;
  		hop_round = 0;
  		
  		state = SEEKING;
  		syncing_count = 0;
  		syncing_success = 0;
  		
  		for(int i = 0; i < window_size / 2; ++i){
  		  next_channel();
  		}
    }

    /*
     * Our virtual destructor.
     */
    multihop_rendezvous_impl::~multihop_rendezvous_impl()
    {
      delete rng;
    }

    int
    multihop_rendezvous_impl::work(int noutput_items,
			  gr_vector_const_void_star &input_items,
			  gr_vector_void_star &output_items)
    {
        const int *in = (const int *) input_items[0];
        
        for(int i = 0; i < noutput_items; ++i){
          int received_channel = in[i];
          //if(received_channel >= num_channels){
            //std::cout << "Decrementing " << received_channel << std::endl;
          //  received_channel = received_channel - num_channels;
          //}
          int expected_channel = next_channel();
          
          if(received_channel != expected_channel){
				      std::cout << "Expected " << expected_channel << " but got " << received_channel << std::endl;
				      //if(state == DONE) state = SEEKING;
				  }
          
  	      if(state == SYNCING){
  	        syncing_count++;
  	        if(received_channel == expected_channel){
  	          syncing_success += 1;
  	          std::cout << "SYNCING SUCCESS: " << syncing_success << " / " << syncing_count << std::endl;
  	        }
  	        if(syncing_success >= REQUIRED_SYNC_HOPS){
  	          state = DONE;
  	          finished = hop_round;
  	        }
  	        if(syncing_count > MAX_SYNC_HOPS){
  	          state = SEEKING;
  	          std::cout << "ABORT SYNCING" << std::endl;
  	        }
  	      }
  	      else if(state == SEEKING){
  	        if(received_channel == expected_channel){
    	        std::cout << "BEGIN SYNCING" << std::endl;
    	        state = SYNCING;
				      syncing_count = 0;
				      syncing_success = 0;
				    }
          }
          else if(state == DONE){
            std::cout << "SYNC COMPLETE AT " << finished << std::endl;
          }
       }

  	    //pauseForHop();
        
        return noutput_items;
    }
    
    void multihop_rendezvous_impl::update_sliding_window() {
		  while (last_window_update <= hop_round) {
			  sliding_window[last_window_update % max_window_size] = rng->next() % num_channels;
			  last_window_update++;
		  }
	  }
	  
    int multihop_rendezvous_impl::next_channel(){
      hop_round += 1;
      std::cout << "Round " << hop_round << " ";
      update_sliding_window();
		  switch (state) {
		  case DONE:
		  case SYNCING:
			    sliding_index = (sliding_index + 1) % max_window_size;
			 break;
		  case SEEKING:
		      if(hop_round % search_rate != 0)
				    sliding_index = (sliding_index + 1) % max_window_size;
			  break;
		  default:
			  throw std::invalid_argument("Invalid state received");
		  }
		  /*std::cout << "Sliding Window: ";
		  for(int i = 0; i < max_window_size; ++i){
		    std::cout << sliding_window[i];
		    if(i == sliding_index) std::cout << "*";
		    std::cout << ", ";
		  }
		  std::cout << std::endl;*/
		  return sliding_window[sliding_index];
    }

  } /* namespace multihop */
} /* namespace gr */

