# Copyright 2011 Free Software Foundation, Inc.
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
install(FILES
    multihop_channel_selector.xml
    multihop_max_cf.xml
    multihop_freqhop_signal_source.xml
    multihop_csprng.xml
    multihop_mode_ii.xml
    multihop_channel_to_freq.xml
    multihop_freq_to_channel.xml
    multihop_multihop_rendezvous.xml
    multihop_modular_clock_rendezvous.xml
    DESTINATION share/gnuradio/grc/blocks
)
