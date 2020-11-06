require 'json'
require_relative './storm'

init = {
  "conf" => {
    "topology.message.timeout.secs" => 1,
  },
  "pidDir" => "/tmp/bolt",
  "context" => {
    "taskid" => 1,
  }
}

puts JSON.dump(init)
puts "end"
STDOUT.flush

i = 0
loop do
  i = i + 1

  h = {
    "id" => i,
    "comp" => i.to_s,
    "task" => 1,
    "stream" => "default",
    "tuple" => [STDIN.readline.chomp]
  }

  puts JSON.dump(h)
  puts "end"
  STDOUT.flush
end
