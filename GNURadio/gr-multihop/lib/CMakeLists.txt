# Copyright 2011,2012 Free Software Foundation, Inc.
#
# This file is part of GNU Radio
#
# GNU Radio is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 3, or (at your option)
# any later version.
#
# GNU Radio is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with GNU Radio; see the file COPYING.  If not, write to
# the Free Software Foundation, Inc., 51 Franklin Street,
# Boston, MA 02110-1301, USA.

########################################################################
# Setup library
########################################################################
include(GrPlatform) #define LIB_SUFFIX

include_directories(${Boost_INCLUDE_DIR})
link_directories(${Boost_LIBRARY_DIRS})
list(APPEND multihop_sources
    channel_selector_impl.cc
    max_cf_impl.cc
    freqhop_signal_source_impl.cc
    csprng_impl.cc
    mode_ii_impl.cc
    channel_to_freq_impl.cc
    freq_to_channel_impl.cc
    multihop_rendezvous_impl.cc
    modular_clock_rendezvous_impl.cc
    )

set(multihop_sources "${multihop_sources}" PARENT_SCOPE)
if(NOT multihop_sources)
	MESSAGE(STATUS "No C++ sources... skipping lib/")
	return()
endif(NOT multihop_sources)

add_library(gnuradio-multihop SHARED ${multihop_sources})
target_link_libraries(gnuradio-multihop ${Boost_LIBRARIES} ${GNURADIO_ALL_LIBRARIES})
set_target_properties(gnuradio-multihop PROPERTIES DEFINE_SYMBOL "gnuradio_multihop_EXPORTS")

if(APPLE)
    set_target_properties(gnuradio-multihop PROPERTIES
        INSTALL_NAME_DIR "${CMAKE_INSTALL_PREFIX}/lib"
    )
endif(APPLE)

########################################################################
# Install built library files
########################################################################
install(TARGETS gnuradio-multihop
    LIBRARY DESTINATION lib${LIB_SUFFIX} # .so/.dylib file
    ARCHIVE DESTINATION lib${LIB_SUFFIX} # .lib file
    RUNTIME DESTINATION bin              # .dll file
)

########################################################################
# Build and register unit test
########################################################################

