require 'json'

loop do
  sleep(1)
  puts JSON.dump({ "command" => "next" })
  puts "end"
  STDOUT.flush
end
