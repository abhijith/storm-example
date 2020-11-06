require_relative "../storm"

class NameSpout < Storm::Spout

  def nextTuple
    s = [{ id: 1, name: "a" },
         { id: 2, name: "b" },
         { id: 3, name: "c" },
         { id: 4, name: "d" }]
    payload = s.sample
    sleep(5)
    emit([payload[:id], payload[:name]])
  end

end

NameSpout.new.run
