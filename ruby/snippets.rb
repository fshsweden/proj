# put the first two words in a and b and the rest in arr

a,b,*arr = *%w{a dog was following me, but then he decided to chase bob}

# this holds for method definitions to
def catall(first, *rest)
  rest.map { |word| first + word }
end

puts catall( 'franken', 'stein', 'berry', 'sense' ) 
	#=> [ 'frankenstein', 'frankenberry','frankensense' ]

def flexi(first, *rest)
  rest.map { |word| first + word }
end

puts flexi( 'hej', 'lite', 'mer', 'här' ) 


 # Blocks, procs, and lambdas. Live and breathe them.

 # know how to pack them into an object
 myblock = lambda { |e| puts e }

 # unpack them for a method
 %w{ and then what? }.each(&myblock)
 
 # create them as needed
 %w{ I saw a ghost! }.each { |w| puts w.upcase }
 
 # and from the method side, how to call them
 def ok
   yield :ok
 end
 
 # or pack them into a block to give to someone else
 def ok_dokey_ok(&block)
    ok(&block)
    block[:dokey] # same as block.call(:dokey)
    ok(&block)
 end
 
 
 # know where the parentheses go when a method takes arguments and a block.
 %w{ a bunch of words }.inject(0) { |size,w| size + 1 } #=> 4
 
 pusher = lambda { |array, word| array.unshift(word) }
 
 %w{ eat more fish }.inject([], &pusher) #=> ['fish', 'more', 'eat' ]

