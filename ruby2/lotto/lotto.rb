
# Ruby Lotto test!

require 'Set'


def get_lotto
	picked_numbers = Set.new
	while picked_numbers.size < 7
		num = 1 + Random.rand(35)
		if (picked_numbers.include?(num))
			#puts "Number #{num} already taken"
		else
			picked_numbers.add(num)
			#puts "Selected #{num}"
		end
	end
	return picked_numbers
end

def num_correct(correct_row, my_row)
	n = 0
	#puts "Size of my_row is #{my_row.keys.size}"
	my_row.each do |nn|
		if correct_row.include?(nn)
			n = n + 1
		end
	end
	return n
end

corr = get_lotto

tries = 0
loop do 
	my_row = get_lotto
	correct = num_correct(corr, my_row)
	tries = tries + 1
	if tries % 1000000 == 0
		puts "1 miljon försök..."
	end

	break if correct == 7
end

puts "7 rätt efter #{tries} försök!"
puts "Correct numbers were:"
corr.each do |i|
	puts "#{i}"
end



