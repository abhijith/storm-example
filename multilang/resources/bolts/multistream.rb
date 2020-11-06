require_relative "../storm"

class Foo < Storm::Bolt
  def process(tup)
    emit([tup.attributes], stream: "name-bolt-stream")
    emit([tup.attributes], stream: "nickname-bolt-stream")
  end
end

Foo.new.run
