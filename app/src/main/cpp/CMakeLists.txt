cmake_minimum_required(VERSION 3.18.1)

project("shkuba")

# Add include directories
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/logic)

# Create the shared library
add_library(
    shkuba
    SHARED
    shkuba_jni.cpp
    logic/board.cpp
    logic/round.cpp
    logic/card.cpp
    logic/deck.cpp
    logic/hand.cpp
    logic/pile.cpp
)

# Find required libraries
find_library(
    log-lib
    log
)

# Link libraries
target_link_libraries(
    shkuba
    ${log-lib}
)

# Set C++ standard
set_target_properties(shkuba PROPERTIES
    CXX_STANDARD 17
    CXX_STANDARD_REQUIRED ON
)
