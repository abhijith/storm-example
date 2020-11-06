require_relative "../storm"

class FooSpout < Storm::Spout

  def nextTuple
    names = [{ id: 1, name: "a" },
             { id: 2, name: "b" },
             { id: 3, name: "c" },
             { id: 4, name: "d" }]

    nicks = [{ id: 1, nickname: "nick-a" },
             { id: 2, nickname: "nick-b" },
             { id: 3, nickname: "nick-c" },
             { id: 4, nickname: "nick-d" }]

    payload = names.sample
    emit([payload[:id], payload[:name]], stream: "name-stream")

    payload = nicks.sample
    emit([payload[:id], payload[:nickname]], stream: "nickname-stream")

    sleep(5)
  end

end

FooSpout.new.run
